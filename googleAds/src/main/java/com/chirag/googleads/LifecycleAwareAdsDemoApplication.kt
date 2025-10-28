package com.chirag.googleads

import android.app.Application
import com.chirag.googleads.util.Logger
import com.chirag.googleads.base.ApplicationLifecycleManager

/**
 * Demo Application class for Google Ads Module
 * 
 * This class demonstrates how to initialize the ApplicationLifecycleManager
 * in the Google Ads module independently.
 * 
 * Note: In a real app, you would initialize this in your main Application class
 * or use the main app's ApplicationLifecycleManager if available.
 */
class LifecycleAwareAdsDemoApplication : Application() {
    
    companion object {
        private const val TAG = "AdsDemoApplication"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        Logger.i(TAG, "🚀 LifecycleAwareAdsDemoApplication created")
        
        // Initialize the ApplicationLifecycleManager for the Google Ads module
        initializeLifecycleManager()
        
        // Initialize the LifecycleAwareAdsManager
        initializeAdsManager()
    }
    
    private fun initializeLifecycleManager() {
        try {
            ApplicationLifecycleManager.getInstance().initialize(this)
            Logger.i(TAG, "✅ ApplicationLifecycleManager initialized successfully")
        } catch (e: Exception) {
            Logger.e(TAG, "❌ Error initializing ApplicationLifecycleManager: ${e.message}", e)
        }
    }
    
    private fun initializeAdsManager() {
        try {
            val adsManager = LifecycleAwareAdsManager.getInstance()
            adsManager.initialize()
            Logger.i(TAG, "✅ LifecycleAwareAdsManager initialized successfully")
        } catch (e: Exception) {
            Logger.e(TAG, "❌ Error initializing LifecycleAwareAdsManager: ${e.message}", e)
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        
        Logger.i(TAG, "🗑️ LifecycleAwareAdsDemoApplication terminating")
        
        // Cleanup lifecycle-aware components
        try {
            val adsManager = LifecycleAwareAdsManager.getInstance()
            adsManager.cleanup()
            Logger.i(TAG, "✅ LifecycleAwareAdsManager cleaned up")
        } catch (e: Exception) {
            Logger.e(TAG, "❌ Error cleaning up LifecycleAwareAdsManager: ${e.message}", e)
        }
    }
} 