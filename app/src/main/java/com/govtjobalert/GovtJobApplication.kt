package com.govtjobalert

import android.app.Application
import com.google.firebase.FirebaseApp

class GovtJobApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            // Firebase init might fail if google-services.json is missing or incorrect
            e.printStackTrace()
        }
    }
}
