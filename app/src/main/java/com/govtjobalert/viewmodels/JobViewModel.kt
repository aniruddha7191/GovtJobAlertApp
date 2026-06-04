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

class JobViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val jobsCollection = db.collection("jobs")

    private val _jobs = MutableStateFlow<List<JobModel>>(emptyList())
    val jobs: StateFlow<List<JobModel>> = _jobs

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchJobs()
    }

    fun fetchJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Fetch only published jobs, ordered by creation date descending
                val snapshot = jobsCollection
                    .whereEqualTo("isPublished", true)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val jobList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(JobModel::class.java)?.copy(id = doc.id)
                }
                _jobs.value = jobList
            } catch (e: Exception) {
                // If offline, Firestore might return cached data automatically or throw an error
                // We'll fallback to cache
                fetchFromCache()
                if (_jobs.value.isEmpty()) {
                    _error.value = "Failed to load jobs. Please check your internet connection."
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchFromCache() {
        try {
            val snapshot = jobsCollection
                .whereEqualTo("isPublished", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get(com.google.firebase.firestore.Source.CACHE)
                .await()
            val jobList = snapshot.documents.mapNotNull { doc ->
                doc.toObject(JobModel::class.java)?.copy(id = doc.id)
            }
            _jobs.value = jobList
        } catch (e: Exception) {
            // Cache read failed
        }
    }

    fun searchJobs(query: String): List<JobModel> {
        if (query.isBlank()) return _jobs.value
        val lowerQuery = query.lowercase()
        return _jobs.value.filter {
            it.title.lowercase().contains(lowerQuery) ||
            it.department.lowercase().contains(lowerQuery) ||
            it.qualification.lowercase().contains(lowerQuery)
        }
    }
    
    fun getJobById(id: String): JobModel? {
        return _jobs.value.find { it.id == id }
    }
}
