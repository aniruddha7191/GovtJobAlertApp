package com.govtjobalert.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import com.govtjobalert.models.JobModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val jobsCollection = db.collection("jobs")
    private var listenerRegistration: ListenerRegistration? = null

    private val _allJobs = MutableStateFlow<List<JobModel>>(emptyList())
    // Not directly exposing to UI usually, but keep for lookup
    val allJobs: StateFlow<List<JobModel>> = _allJobs

    private val _selectedCategory = MutableStateFlow("All Jobs")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val filteredJobs = combine(_allJobs, _selectedCategory) { jobs, category ->
        if (category == "All Jobs" || category.isBlank()) jobs
        else jobs.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalJobsCount = combine(_allJobs, _selectedCategory) { jobs, category ->
        if (category == "All Jobs" || category.isBlank()) jobs.size
        else jobs.count { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val publishedJobsCount = combine(_allJobs, _selectedCategory) { jobs, category ->
        if (category == "All Jobs" || category.isBlank()) jobs.count { it.isPublished }
        else jobs.count { it.category == category && it.isPublished }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        setupListener()
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    private fun setupListener() {
        _isLoading.value = true
        listenerRegistration?.remove()
        
        listenerRegistration = jobsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    android.util.Log.e("AdminViewModel", "Firestore Error: ${e.message}", e)
                    _isLoading.value = false
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val jobList = snapshot.documents.mapNotNull { doc ->
                        val job = doc.toObject(JobModel::class.java)
                        if (job != null) {
                            val legacyPublished = doc.getBoolean("published")
                            if (legacyPublished != null && !doc.contains("isPublished")) {
                                job.isPublished = legacyPublished
                                doc.reference.update(
                                    "isPublished", legacyPublished,
                                    "published", com.google.firebase.firestore.FieldValue.delete()
                                )
                            }
                            job.copy(id = doc.id)
                        } else null
                    }
                    _allJobs.value = jobList
                }
                _isLoading.value = false
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun fetchAllJobs() {}

    fun addJob(job: JobModel, onSuccess: () -> Unit, onError: (String) -> Unit) {
        jobsCollection.add(job)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to add job") }
    }

    fun updateJob(job: JobModel, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (job.id.isBlank()) {
            onError("Invalid Job ID. Cannot update.")
            return
        }
        jobsCollection.document(job.id).set(job)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to update job") }
    }

    fun deleteJob(jobId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (jobId.isBlank()) {
            onError("Invalid Job ID. Cannot delete.")
            return
        }
        jobsCollection.document(jobId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Failed to delete job") }
    }
    
    fun getJobById(id: String): JobModel? {
        return _allJobs.value.find { it.id == id }
    }
}
