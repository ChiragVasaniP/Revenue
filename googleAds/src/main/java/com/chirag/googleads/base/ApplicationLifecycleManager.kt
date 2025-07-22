package com.chirag.googleads.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Application Lifecycle Manager for Google Ads Module
 * 
 * This is a simplified version of the ApplicationLifecycleManager that can be used
 * independently in the Google Ads module without depending on the main app module.
 * 
 * Usage:
 * 1. Initialize: ApplicationLifecycleManager.getInstance().initialize(application)
 * 2. Register listener: ApplicationLifecycleManager.getInstance().registerLifecycleListener(yourObject)
 * 3. Implement ApplicationLifecycleListener in your object
 */
class ApplicationLifecycleManager private constructor() {
    
    companion object {
        private const val TAG = "AdsLifecycleManager"
        
        @Volatile
        private var instance: ApplicationLifecycleManager? = null
        
        fun getInstance(): ApplicationLifecycleManager {
            return instance ?: synchronized(this) {
                instance ?: ApplicationLifecycleManager().also { instance = it }
            }
        }
    }
    
    private val lifecycleListeners = CopyOnWriteArrayList<ApplicationLifecycleListener>()
    private var isAppInForeground = false
    private var activityCount = 0
    private var isInitialized = false
    
    /**
     * Initialize the lifecycle manager with the application instance
     * Call this in your Application.onCreate()
     */
    fun initialize(application: Application) {
        if (isInitialized) {
            Log.w("chirag_lifecycle_", "ApplicationLifecycleManager already initialized")
            return
        }
        
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(@NonNull activity: Activity, @Nullable savedInstanceState: Bundle?) {
                Log.d("chirag_lifecycle_", "onActivityCreated: ${activity.javaClass.name}")
                notifyActivityCreated(activity)
            }
            
            override fun onActivityStarted(@NonNull activity: Activity) {
                Log.d("chirag_lifecycle_", "onActivityStarted: ${activity.javaClass.name}")
                activityCount++
                if (!isAppInForeground) {
                    isAppInForeground = true
                    notifyAppStarted()
                }
                notifyActivityStarted(activity)
            }
            
            override fun onActivityResumed(@NonNull activity: Activity) {
                Log.d("chirag_lifecycle_", "onActivityResumed: ${activity.javaClass.name}")
                notifyActivityResumed(activity)
            }
            
            override fun onActivityPaused(@NonNull activity: Activity) {
                Log.d("chirag_lifecycle_", "onActivityPaused: ${activity.javaClass.name}")
                notifyActivityPaused(activity)
            }
            
            override fun onActivityStopped(@NonNull activity: Activity) {
                Log.d("chirag_lifecycle_", "onActivityStopped: ${activity.javaClass.name}")
                activityCount--
                if (activityCount == 0) {
                    isAppInForeground = false
                    notifyAppStopped()
                }
                notifyActivityStopped(activity)
            }
            
            override fun onActivitySaveInstanceState(@NonNull activity: Activity, @NonNull outState: Bundle) {
                Log.d("chirag_lifecycle_", "onActivitySaveInstanceState: ${activity.javaClass.name}")
                notifyActivitySaveInstanceState(activity, outState)
            }
            
            override fun onActivityDestroyed(@NonNull activity: Activity) {
                Log.d("chirag_lifecycle_", "onActivityDestroyed: ${activity.javaClass.name}")
                notifyActivityDestroyed(activity)
            }
        })
        
        isInitialized = true
        Log.i("chirag_lifecycle_", "ApplicationLifecycleManager initialized successfully")
    }
    
    /**
     * Register a lifecycle listener
     */
    fun registerLifecycleListener(listener: ApplicationLifecycleListener) {
        if (listener != null && !lifecycleListeners.contains(listener)) {
            lifecycleListeners.add(listener)
            Log.d(TAG, "Registered lifecycle listener: ${listener.javaClass.simpleName}")
        }
    }
    
    /**
     * Unregister a lifecycle listener
     */
    fun unregisterLifecycleListener(listener: ApplicationLifecycleListener) {
        if (listener != null) {
            lifecycleListeners.remove(listener)
            Log.d(TAG, "Unregistered lifecycle listener: ${listener.javaClass.simpleName}")
        }
    }
    
    /**
     * Check if app is currently in foreground
     */
    fun isAppInForeground(): Boolean = isAppInForeground
    
    /**
     * Get current activity count
     */
    fun getActivityCount(): Int = activityCount
    
    /**
     * Check if the manager is initialized
     */
    fun isInitialized(): Boolean = isInitialized
    
    // Notification methods
    private fun notifyAppStarted() {
        Log.d(TAG, "App started - notifying ${lifecycleListeners.size} listeners")
        for (listener in lifecycleListeners) {
            try {
                listener.onAppStarted()
            } catch (e: Exception) {
                Log.e(TAG, "Error in onAppStarted for ${listener.javaClass.simpleName}", e)
            }
        }
    }
    
    private fun notifyAppStopped() {
        Log.d(TAG, "App stopped - notifying ${lifecycleListeners.size} listeners")
        for (listener in lifecycleListeners) {
            try {
                listener.onAppStopped()
            } catch (e: Exception) {
                Log.e(TAG, "Error in onAppStopped for ${listener.javaClass.simpleName}", e)
            }
        }
    }
    
    private fun notifyActivityCreated(activity: Activity) {
        for (listener in lifecycleListeners) {
            try {
                listener.onActivityCreated(activity)
            } catch (e: Exception) {
                Log.e(TAG, "Error in onActivityCreated for ${listener.javaClass.simpleName}", e)
            }
        }
    }
    
    private fun notifyActivityStarted(activity: Activity) {
        for (listener in lifecycleListeners) {
            try {
                listener.onActivityStarted(activity)
            } catch (e: Exception) {
                Log.e(TAG, "Error in onActivityStarted for ${listener.javaClass.simpleName}", e)
            }
        }
    }
    
    private fun notifyActivityResumed(activity: Activity) {
        for (listener in lifecycleListeners) {
            try {
                listener.onActivityResumed(activity)
            } catch (e: Exception) {
                Log.e(TAG, "Error in onActivityResumed for ${listener.javaClass.simpleName}", e)
            }
        }
    }
    
    private fun notifyActivityPaused(activity: Activity) {
        for (listener in lifecycleListeners) {
            try {
                listener.onActivityPaused(activity)
            } catch (e: Exception) {
                Log.e(TAG, "Error in onActivityPaused for ${listener.javaClass.simpleName}", e)
            }
        }
    }
    
    private fun notifyActivityStopped(activity: Activity) {
        for (listener in lifecycleListeners) {
            try {
                listener.onActivityStopped(activity)
            } catch (e: Exception) {
                Log.e(TAG, "Error in onActivityStopped for ${listener.javaClass.simpleName}", e)
            }
        }
    }
    
    private fun notifyActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        for (listener in lifecycleListeners) {
            try {
                listener.onActivitySaveInstanceState(activity, outState)
            } catch (e: Exception) {
                Log.e(TAG, "Error in onActivitySaveInstanceState for ${listener.javaClass.simpleName}", e)
            }
        }
    }
    
    private fun notifyActivityDestroyed(activity: Activity) {
        for (listener in lifecycleListeners) {
            try {
                listener.onActivityDestroyed(activity)
            } catch (e: Exception) {
                Log.e(TAG, "Error in onActivityDestroyed for ${listener.javaClass.simpleName}", e)
            }
        }
    }
} 