package com.chirag.googleads.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.chirag.googleads.adsUtil.AppOpenAdManager
import com.chirag.googleads.adsUtil.OnShowAdCompleteListener
import com.chirag.googleads.event.Logger
import com.chirag.googleads.localcache.PreferenceManager

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
class ApplicationLifecycleManagerOpenAds private constructor() {
    private lateinit var appOpenAdManager: AppOpenAdManager


    companion object {
        private const val TAG = "AdsLifecycleManager"
        
        @Volatile
        private var instance: ApplicationLifecycleManagerOpenAds? = null
        
        fun getInstance(): ApplicationLifecycleManagerOpenAds {
            return instance ?: synchronized(this) {
                instance ?: ApplicationLifecycleManagerOpenAds().also { instance = it }
            }
        }
    }
    
    private var isAppInForeground = false
    private var activityCount = 0
    private var isInitialized = false

    var currentActivity: Activity? = null


    fun applicationIsInStart() {
        currentActivity?.let {
            appOpenAdManager.showAdIfAvailable(it)

        }
    }
    
    /**
     * Initialize the lifecycle manager with the application instance
     * Call this in your Application.onCreate()
     */
    fun initialize(application: Application) {
        PreferenceManager.init(application)

        if (isInitialized) {
            Logger.w("chirag_lifecycle_", "ApplicationLifecycleManager already initialized")
            return
        }

        ApplicationLifecycleManager.getInstance().initialize(application)
        initizeApplicationProcess()

        appOpenAdManager = AppOpenAdManager(application)
        
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(@NonNull activity: Activity, @Nullable savedInstanceState: Bundle?) {
                Logger.d("chirag_lifecycle_", "onActivityCreated: ${activity.javaClass.name}")
            }
            
            override fun onActivityStarted(@NonNull activity: Activity) {
                Logger.d("chirag_lifecycle_", "onActivityStarted: ${activity.javaClass.name}")
                activityCount++
                if (!isAppInForeground) {
                    isAppInForeground = true
                }

                if (!appOpenAdManager.isShowingAd) {
                    currentActivity = activity
                }
            }
            
            override fun onActivityResumed(@NonNull activity: Activity) {
                Logger.d("chirag_lifecycle_", "onActivityResumed: ${activity.javaClass.name}")
            }
            
            override fun onActivityPaused(@NonNull activity: Activity) {
                Logger.d("chirag_lifecycle_", "onActivityPaused: ${activity.javaClass.name}")
            }
            
            override fun onActivityStopped(@NonNull activity: Activity) {
                Logger.d("chirag_lifecycle_", "onActivityStopped: ${activity.javaClass.name}")
                activityCount--
                if (activityCount == 0) {
                    isAppInForeground = false
                }
            }
            
            override fun onActivitySaveInstanceState(@NonNull activity: Activity, @NonNull outState: Bundle) {
                Logger.d("chirag_lifecycle_", "onActivitySaveInstanceState: ${activity.javaClass.name}")
            }
            
            override fun onActivityDestroyed(@NonNull activity: Activity) {
                Logger.d("chirag_lifecycle_", "onActivityDestroyed: ${activity.javaClass.name}")
            }
        })
        
        isInitialized = true
        Logger.i("chirag_lifecycle_", "ApplicationLifecycleManager initialized successfully")
    }

    private fun initizeApplicationProcess() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object:DefaultLifecycleObserver{
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                applicationIsInStart()
            }
        })
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

    /**
     * Shows an app open ad.
     *
     * @param activity the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
    }

    fun loadAd(context: Activity) {
        // We wrap the loadAd to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.loadAd(context)
    }


} 