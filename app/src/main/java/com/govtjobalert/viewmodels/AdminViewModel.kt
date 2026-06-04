package com.govtjobalert.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import com.govtjobalert.models.JobModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
                    _isLoading.value = false
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val jobList = snapshot.toObjects(JobModel::class.java)
                    _allJobs.value = jobList
                }
                _isLoading.value = false
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun fetchAllJobs() {
        // Handled automatically by snapshot listener
    }

    fun addJob(job: JobModel, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Let Firestore auto-generate ID using .add()
                jobsCollection.add(job).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to add job")
            }
        }
    }

    fun updateJob(job: JobModel, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Ensure we are using the exact Firestore document ID
                jobsCollection.document(job.id).set(job).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to update job")
            }
        }
    }

    fun deleteJob(jobId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Permanently delete the document by its exact ID
                jobsCollection.document(jobId).delete().await()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to delete job")
            }
        }
    }
    
    fun getJobById(id: String): JobModel? {
        return _allJobs.value.find { it.id == id }
    }
}
