package com.govtjobalert

import android.app.Application
import com.google.firebase.FirebaseApp

import com.govtjobalert.utils.BookmarkManager

class GovtJobApplication : Application() {
    companion object {
        lateinit var bookmarkManager: BookmarkManager
            private set
    }

    override fun onCreate() {
        super.onCreate()
        bookmarkManager = BookmarkManager(this)
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            // Firebase init might fail if google-services.json is missing or incorrect
            e.printStackTrace()
        }
    }
}
