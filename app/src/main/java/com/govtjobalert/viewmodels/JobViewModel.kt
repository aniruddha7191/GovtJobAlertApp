package com.govtjobalert.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import com.govtjobalert.models.JobModel
import com.govtjobalert.GovtJobApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class JobViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val jobsCollection = db.collection("jobs")
    private var listenerRegistration: ListenerRegistration? = null
    
    val bookmarkManager = GovtJobApplication.bookmarkManager

    private val _jobs = MutableStateFlow<List<JobModel>>(emptyList())
    val allJobs: StateFlow<List<JobModel>> = _jobs

    private val _selectedCategory = MutableStateFlow("All Jobs")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val filteredJobs = combine(_jobs, _selectedCategory) { jobs, category ->
        if (category == "All Jobs" || category.isBlank()) jobs
        else jobs.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        setupListener()
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setupListener() {
        _isLoading.value = true
        _error.value = null
        listenerRegistration?.remove()
        
        listenerRegistration = jobsCollection
            .whereEqualTo("isPublished", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    android.util.Log.e("JobViewModel", "Firestore Error: ${e.message}", e)
                    _error.value = "Firestore Error: ${e.localizedMessage ?: e.message}"
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
                            }
                            job.copy(id = doc.id)
                        } else null
                    }
                    _jobs.value = jobList
                    _error.value = null
                }
                _isLoading.value = false
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun fetchJobs() {
        setupListener()
    }

    fun searchJobs(query: String): List<JobModel> {
        val jobsToSearch = filteredJobs.value
        if (query.isBlank()) return jobsToSearch
        val lowerQuery = query.lowercase()
        return jobsToSearch.filter {
            it.title.lowercase().contains(lowerQuery) ||
            it.department.lowercase().contains(lowerQuery) ||
            it.category.lowercase().contains(lowerQuery) ||
            it.qualification.lowercase().contains(lowerQuery)
        }
    }
    
    fun getJobById(id: String): JobModel? {
        return _jobs.value.find { it.id == id }
    }
    
    fun getSavedJobs(savedIds: Set<String>): List<JobModel> {
        return _jobs.value.filter { savedIds.contains(it.id) }
    }
}
