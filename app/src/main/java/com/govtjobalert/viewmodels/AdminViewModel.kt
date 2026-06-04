package com.govtjobalert.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import com.govtjobalert.models.JobModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val jobsCollection = db.collection("jobs")
    private var listenerRegistration: ListenerRegistration? = null

    private val _allJobs = MutableStateFlow<List<JobModel>>(emptyList())
    val allJobs: StateFlow<List<JobModel>> = _allJobs

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        setupListener()
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
                    // Explicitly map document ID to ensure deletion/editing works
                    val jobList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(JobModel::class.java)?.copy(id = doc.id)
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
        // Use listeners instead of coroutine await() to prevent CancellationExceptions
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
