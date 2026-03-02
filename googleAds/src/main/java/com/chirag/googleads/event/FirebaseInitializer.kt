/*
package com.chirag.googleads.event

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.initialize
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.provider.FirebaseInitProvider

object FirebaseInitializer {

    private var firebaseAnalytics: FirebaseAnalytics? = null
//    private var isInitialized = false
    private const val LIB_FIREBASE_APP_NAME = "DachiwareLibApp"

    fun init(context: Context) {
//        Logger.d("FirebaseInitializer", "init $isInitialized")

//        if (isInitialized) return

        try {
            // Use application context
            val appContext = context.applicationContext

            // Check if default Firebase app exists
            val existingDefaultApp = FirebaseApp.getApps(appContext).find {
                it.name == LIB_FIREBASE_APP_NAME
            }
            Logger.d("FirebaseInitializer", "Using host app's Firebase existingDefaultApp $existingDefaultApp")

            if (existingDefaultApp != null) {
                // If host app has Firebase, use their initialization
                firebaseAnalytics = FirebaseAnalytics.getInstance(appContext)
//                isInitialized = true
                Logger.d("FirebaseInitializer", "Using host app's Firebase configuration")
                return
            }

            // Initialize our custom Firebase app for the library
            initializeCustomFirebase(appContext)

        } catch (e: Exception) {
            Logger.e("FirebaseInitializer", "Firebase initialization failed", e)
        }
    }

    private fun initializeCustomFirebase(context: Context) {
        try {
            val options = FirebaseOptions.Builder()
                .setProjectId("fir-chirag-projects")
                .setApplicationId("1:1062841038778:android:202ee79a23b33248ec8f83")
                .setApiKey("AIzaSyDeZ_HJI8Xo7I4I7EU8A5T2EVmuMZzFB9g")
                .setGcmSenderId("1062841038778")
                .setStorageBucket("fir-chirag-projects.firebasestorage.app")
                .build()

            // Initialize custom Firebase app
            val firebaseApp = FirebaseApp.initializeApp(context, options, LIB_FIREBASE_APP_NAME)

            Firebase.initialize(context, options, LIB_FIREBASE_APP_NAME)
            FirebaseInstallations.getInstance(firebaseApp)

            // Force Firebase Analytics to use our custom app
            firebaseAnalytics = FirebaseAnalytics.getInstance(context)

            val firebase= Firebase.initialize(context,options)
           val app = FirebaseApp.getInstance(LIB_FIREBASE_APP_NAME)
//            isInitialized = true

            Logger.d("FirebaseInitializer", "Custom Firebase initialized successfully")
            Logger.d("FirebaseInitializer", "Firebase App: ${firebaseApp.name}")
            Logger.d("FirebaseInitializer", "Project: ${firebaseApp.options.projectId}")

        } catch (e: Exception) {
            Logger.e("FirebaseInitializer", "Custom Firebase initialization failed", e)
        }
    }

    internal fun logEvent(eventName: String, params: Map<String, Any>? = null) {
//        if (!isInitialized) {
//            Logger.w("FirebaseInitializer", "Firebase not initialized - event '$eventName' ignored")
//            return
//        }

        try {
            Firebase.analytics?.logEvent(eventName, params?.toBundle())
            Logger.v("FirebaseInitializer", "Event logged: $eventName")
        } catch (e: Exception) {
            Logger.e("FirebaseInitializer", "Failed to log event: $eventName", e)
        }
    }

    // Extension function to convert Map to Bundle
    private fun Map<String, Any>.toBundle(): android.os.Bundle {
        return android.os.Bundle().apply {
            forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }
    }

//    internal fun isInitialized(): Boolean = isInitialized
}*/
