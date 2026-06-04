package com.govtjobalert.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.govtjobalert.models.JobModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val jobsCollection = db.collection("jobs")

    private val _allJobs = MutableStateFlow<List<JobModel>>(emptyList())
    val allJobs: StateFlow<List<JobModel>> = _allJobs

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchAllJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = jobsCollection
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val jobList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(JobModel::class.java)?.copy(id = doc.id)
                }
                _allJobs.value = jobList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addJob(job: JobModel, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Generate new ID
                val newDoc = jobsCollection.document()
                val jobWithId = job.copy(id = newDoc.id)
                newDoc.set(jobWithId).await()
                
                // Refresh list
                fetchAllJobs()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to add job")
            }
        }
    }

    fun updateJob(job: JobModel, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                jobsCollection.document(job.id).set(job).await()
                fetchAllJobs()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to update job")
            }
        }
    }

    fun deleteJob(jobId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                jobsCollection.document(jobId).delete().await()
                fetchAllJobs()
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
