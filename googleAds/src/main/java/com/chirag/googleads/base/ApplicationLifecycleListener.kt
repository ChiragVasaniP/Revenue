package com.chirag.googleads.base

import android.app.Activity
import android.os.Bundle

/**
 * Application Lifecycle Listener Interface
 * 
 * Implement this interface in your classes to receive application lifecycle events.
 * This provides a way to attach lifecycle awareness to any object, similar to Activity lifecycle.
 * 
 * All methods are optional - you can override only the ones you need.
 */
interface ApplicationLifecycleListener {
    
    /**
     * Called when the application comes to foreground (first activity started)
     * Similar to Application.onForeground()
     */
    fun onAppStarted() {
        // Optional implementation
    }
    
    /**
     * Called when the application goes to background (all activities stopped)
     * Similar to Application.onBackground()
     */
    fun onAppStopped() {
        // Optional implementation
    }
    
    /**
     * Called when any activity is created
     * Similar to Activity.onCreate()
     */
    fun onActivityCreated(activity: Activity) {
        // Optional implementation
    }
    
    /**
     * Called when any activity is started
     * Similar to Activity.onStart()
     */
    fun onActivityStarted(activity: Activity) {
        // Optional implementation
    }
    
    /**
     * Called when any activity is resumed
     * Similar to Activity.onResume()
     */
    fun onActivityResumed(activity: Activity) {
        // Optional implementation
    }
    
    /**
     * Called when any activity is paused
     * Similar to Activity.onPause()
     */
    fun onActivityPaused(activity: Activity) {
        // Optional implementation
    }
    
    /**
     * Called when any activity is stopped
     * Similar to Activity.onStop()
     */
    fun onActivityStopped(activity: Activity) {
        // Optional implementation
    }
    
    /**
     * Called when any activity saves its instance state
     * Similar to Activity.onSaveInstanceState()
     */
    fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // Optional implementation
    }
    
    /**
     * Called when any activity is destroyed
     * Similar to Activity.onDestroy()
     */
    fun onActivityDestroyed(activity: Activity) {
        // Optional implementation
    }
} 