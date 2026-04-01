package com.chirag.googleads


import android.app.Activity
import android.app.Application
import android.content.res.Resources
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import com.chirag.googleads.adsUtil.AppOpenAdManager
import com.chirag.googleads.adsUtil.OnShowAdCompleteListener
import com.chirag.googleads.base.ApplicationLifecycleManager
import com.chirag.googleads.localcache.PreferenceManager
//import com.chirag.googleads.util.FirebaseResourcesWrapper


/** Application class that initializes, loads and show ads when activities change states. */
//open class MyApplication :
//    Application(), Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
//
//    private lateinit var appOpenAdManager: AppOpenAdManager
//
////    private var firebaseResources: FirebaseResourcesWrapper? = null
//
//    var currentActivity: Activity? = null
//
//
///*    override fun getResources(): Resources? {
//        if (firebaseResources == null) firebaseResources = FirebaseResourcesWrapper(super.getResources())
//        return firebaseResources
//    }*/
//
//    override fun onCreate() {
//        super<Application>.onCreate()
//        MultiDex.install(this@MyApplication)
//        registerActivityLifecycleCallbacks(this)
//        ApplicationLifecycleManager.getInstance().initialize(this)
//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
//        appOpenAdManager = AppOpenAdManager(this@MyApplication)
//
//    }
//
//    /**
//     * DefaultLifecycleObserver method that shows the app open ad when the app moves to foreground.
//     */
//    override fun onStart(owner: LifecycleOwner) {
//        super.onStart(owner)
//        PreferenceManager.init(this@MyApplication)
//        currentActivity?.let {
//            // Show the ad (if available) when the app moves to foreground.
//            appOpenAdManager.showAdIfAvailable(it)
//        }
//    }
//
//    /** ActivityLifecycleCallback methods. */
//    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
//
//    override fun onActivityStarted(activity: Activity) {
//        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
//        // SDK or another activity class implemented by a third party mediation partner. Updating the
//        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
//        // one that shows the ad.
//        if (!appOpenAdManager.isShowingAd) {
//            currentActivity = activity
//        }
//    }
//
//    override fun onActivityResumed(activity: Activity) {}
//
//    override fun onActivityPaused(activity: Activity) {}
//
//    override fun onActivityStopped(activity: Activity) {}
//
//    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
//
//    override fun onActivityDestroyed(activity: Activity) {}
//
//    /**
//     * Shows an app open ad.
//     *
//     * @param activity the activity that shows the app open ad
//     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
//     */
//    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
//        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
//        // class.
//        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener)
//    }
//
//    /**
//     * Load an app open ad.
//     *
//     * @param activity the activity that shows the app open ad
//     */
//    fun loadAd(context: Activity) {
//        // We wrap the loadAd to enforce that other classes only interact with MyApplication
//        // class.
//        appOpenAdManager.loadAd(context)
//    }
//
//
//    companion object {
//
//
//        // Check your logcat output for the test device hashed ID e.g.
//        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
//        // to get test ads on this device" or
//        // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
//        // a debug device".
//        const val TEST_DEVICE_HASHED_ID = "94E478E0C133848F5605B6D42EE2640D"
//    }
//}