package com.govtjobalert.utils

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BookmarkManager(context: Context) {
    private val prefs = context.getSharedPreferences("saved_jobs_prefs", Context.MODE_PRIVATE)
    private val _savedJobIds = MutableStateFlow<Set<String>>(emptySet())
    val savedJobIds: StateFlow<Set<String>> = _savedJobIds.asStateFlow()

    init {
        val saved = prefs.getStringSet("bookmarked_ids", emptySet()) ?: emptySet()
        _savedJobIds.value = saved.toSet()
    }

    fun toggleBookmark(jobId: String): Boolean {
        val currentSet = _savedJobIds.value.toMutableSet()
        val isNowSaved = if (currentSet.contains(jobId)) {
            currentSet.remove(jobId)
            false
        } else {
            currentSet.add(jobId)
            true
        }
        
        prefs.edit().putStringSet("bookmarked_ids", currentSet).apply()
        _savedJobIds.value = currentSet
        return isNowSaved
    }
    
    fun isJobSaved(jobId: String): Boolean {
        return _savedJobIds.value.contains(jobId)
    }
}
