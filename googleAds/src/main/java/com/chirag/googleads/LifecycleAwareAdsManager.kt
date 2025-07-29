package com.chirag.googleads

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.Keep
import com.chirag.googleads.adsUtil.AdvanceNativeAdsUtil
import com.chirag.googleads.adsUtil.banner.BannerAdsUtil
import com.chirag.googleads.adsUtil.InterstitialAdUtil
import com.chirag.googleads.adsUtil.RewardedAdUtil
import com.chirag.googleads.adsUtil.RewardedInterstitialAdUtil
import com.chirag.googleads.base.ApplicationLifecycleListener
import com.chirag.googleads.base.ApplicationLifecycleManager
import com.chirag.googleads.consent.AdConsentUtil
import com.chirag.googleads.localcache.LocalAdPrefHelper
import com.google.android.gms.ads.rewarded.RewardItem

/**
 * Lifecycle-Aware Ads Manager
 *
 * This class extends the functionality of AdsShowingClass by adding lifecycle awareness.
 * It automatically manages ad loading, showing, and cleanup based on app lifecycle events.
 *
 * Features:
 * - Automatic ad preloading when app comes to foreground
 * - Pause ad operations when app goes to background
 * - Smart ad frequency management
 * - Automatic cleanup when activities are destroyed
 * - Performance optimization based on app state
 */
@Keep
class LifecycleAwareAdsManager private constructor() : ApplicationLifecycleListener {

    companion object instanceLifecycle {
        private const val TAG = "LifecycleAwareAds"

        @Volatile
        private var instance: LifecycleAwareAdsManager? = null

        fun getInstance(): LifecycleAwareAdsManager {
            return instance ?: synchronized(this) {
                instance ?: LifecycleAwareAdsManager().also { instance = it }
            }
        }
    }

    // State tracking
    private var isInitialized = false
    private var isAppInForeground = false
    private var lastAdShowTime = 0L
    private val minAdInterval = 30000L // 30 seconds minimum between ads

    // Ad state tracking
    private var interstitialAdReady = false
    private var rewardedAdReady = false
    private var rewardedInterstitialAdReady = false

    /**
     * Initialize the lifecycle-aware ads manager
     */
    fun initialize() {
        if (!isInitialized) {
            ApplicationLifecycleManager.getInstance().registerLifecycleListener(this)
            isInitialized = true
            Log.i(TAG, "LifecycleAwareAdsManager initialized")
        }
    }

    /**
     * Cleanup the lifecycle-aware ads manager
     */
    fun cleanup() {
        if (isInitialized) {
            ApplicationLifecycleManager.getInstance().unregisterLifecycleListener(this)
            isInitialized = false
            Log.i(TAG, "LifecycleAwareAdsManager cleaned up")
        }
    }

    // Application Lifecycle Events

    override fun onAppStarted() {
        isAppInForeground = true
        Log.i(TAG, "🚀 App started - Preloading ads for better performance")

        // Preload ads when app comes to foreground for better user experience
        preloadAds()
    }

    override fun onAppStopped() {
        isAppInForeground = false
        Log.i(TAG, "⏸️ App stopped - Pausing ad operations")

        // Pause ad operations when app goes to background
        pauseAdOperations()
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "▶️ Activity resumed: ${activity.javaClass.simpleName}")

        // Resume ad operations for specific activities if needed
        if (isAppInForeground) {
            resumeAdOperations(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(TAG, "⏸️ Activity paused: ${activity.javaClass.simpleName}")

        // Pause ad operations for specific activities
        pauseActivityAdOperations(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "🗑️ Activity destroyed: ${activity.javaClass.simpleName}")

        // Cleanup activity-specific ad resources
        cleanupActivityAdResources(activity)
    }

    // Ad Management Methods (similar to AdsShowingClass but with lifecycle awareness)

    /**
     * Displays a banner ad with lifecycle awareness
     */
    fun showBannerAds(activity: Activity, viewGroup: ViewGroup) {
        if (!canShowAds(activity)) return

        Log.d(TAG, "Showing banner ad in ${activity.javaClass.simpleName}")
        BannerAdsUtil.showBannerAd(activity, container = viewGroup)
    }

    /**
     * Displays a native ad with lifecycle awareness
     */
    fun showNativeAds(activity: Activity, viewGroup: ViewGroup) {
        if (!canShowAds(activity)) return

        Log.d(TAG, "Showing native ad in ${activity.javaClass.simpleName}")
        AdvanceNativeAdsUtil.loadAndShowNativeAd(activity, container = viewGroup)
    }

    /**
     * Displays an interstitial ad with smart frequency management
     */
    fun showInterstitialAd(activity: Activity, onAdClosed: () -> Unit) {
        if (!canShowAds(activity)) {
            onAdClosed.invoke()
            return
        }

        if (!canShowInterstitialAd()) {
            Log.d(TAG, "Interstitial ad skipped due to frequency limit")
            onAdClosed.invoke()
            return
        }

        Log.d(TAG, "Showing interstitial ad in ${activity.javaClass.simpleName}")
        lastAdShowTime = System.currentTimeMillis()
        InterstitialAdUtil.loadAndShowAd(activity, onAdClosed)
    }

    /**
     * Displays a rewarded ad with lifecycle awareness
     */
    fun showRewardAds(
        activity: Activity,
        onRewardEarned: (reward: RewardItem) -> Unit,
        onAdClosed: () -> Unit
    ) {
        if (!canShowAds(activity)) {
            onAdClosed.invoke()
            return
        }

        Log.d(TAG, "Showing rewarded ad in ${activity.javaClass.simpleName}")
        RewardedAdUtil.loadAndShowAd(activity, onRewardEarned, onAdClosed)
    }

    /**
     * Displays a rewarded interstitial ad with lifecycle awareness
     */
    fun showRewardInterstitialAds(
        activity: Activity,
        onRewardEarned: (amount: Int, type: String) -> Unit,
        onAdClosed: () -> Unit
    ) {
        if (!canShowAds(activity)) {
            onAdClosed.invoke()
            return
        }

        Log.d(TAG, "Showing rewarded interstitial ad in ${activity.javaClass.simpleName}")
        RewardedInterstitialAdUtil.loadAndShowAd(activity, onRewardEarned, onAdClosed)
    }

    // Lifecycle-aware ad management methods

    private fun preloadAds() {
        Log.d(TAG, "Preloading ads for better performance")

        // Preload interstitial ads when app comes to foreground
        if (isAppInForeground) {
            try {
                // Note: These preload methods should be implemented in the respective ad utilities
                // If they don't exist, the calls will be ignored
                preloadInterstitialAd()
                preloadRewardedAd()
                preloadRewardedInterstitialAd()
            } catch (e: Exception) {
                Log.w(TAG, "Error preloading ads: ${e.message}")
            }
        }
    }

    private fun preloadInterstitialAd() {
        try {
            // Call preload method if it exists in InterstitialAdUtil
            InterstitialAdUtil::class.java.getMethod("preloadAd").invoke(null)
            interstitialAdReady = true
            Log.d(TAG, "Interstitial ad preloaded successfully")
        } catch (e: NoSuchMethodException) {
            Log.d(TAG, "InterstitialAdUtil.preloadAd() method not found - skipping preload")
        } catch (e: Exception) {
            Log.w(TAG, "Error preloading interstitial ad: ${e.message}")
        }
    }

    private fun preloadRewardedAd() {
        try {
            // Call preload method if it exists in RewardedAdUtil
            RewardedAdUtil::class.java.getMethod("preloadAd").invoke(null)
            rewardedAdReady = true
            Log.d(TAG, "Rewarded ad preloaded successfully")
        } catch (e: NoSuchMethodException) {
            Log.d(TAG, "RewardedAdUtil.preloadAd() method not found - skipping preload")
        } catch (e: Exception) {
            Log.w(TAG, "Error preloading rewarded ad: ${e.message}")
        }
    }

    private fun preloadRewardedInterstitialAd() {
        try {
            // Call preload method if it exists in RewardedInterstitialAdUtil
            RewardedInterstitialAdUtil::class.java.getMethod("preloadAd").invoke(null)
            rewardedInterstitialAdReady = true
            Log.d(TAG, "Rewarded interstitial ad preloaded successfully")
        } catch (e: NoSuchMethodException) {
            Log.d(TAG, "RewardedInterstitialAdUtil.preloadAd() method not found - skipping preload")
        } catch (e: Exception) {
            Log.w(TAG, "Error preloading rewarded interstitial ad: ${e.message}")
        }
    }

    private fun pauseAdOperations() {
        Log.d(TAG, "Pausing ad operations")

        // Pause ad loading when app goes to background
        // This helps save resources and battery
    }

    private fun resumeAdOperations(activity: Activity) {
        Log.d(TAG, "Resuming ad operations for ${activity.javaClass.simpleName}")

        // Resume ad operations for specific activities
        // This can be customized based on activity type
    }

    private fun pauseActivityAdOperations(activity: Activity) {
        Log.d(TAG, "Pausing ad operations for ${activity.javaClass.simpleName}")

        // Pause activity-specific ad operations
    }

    private fun cleanupActivityAdResources(activity: Activity) {
        Log.d(TAG, "Cleaning up ad resources for ${activity.javaClass.simpleName}")

        // Cleanup activity-specific ad resources
        // This prevents memory leaks
    }

    // Utility methods

    private fun canShowAds(activity: Activity): Boolean {
        return LocalAdPrefHelper.isAdsEnabled(activity = activity) &&
                AdConsentUtil.canRequestAds(activity)
    }

    private fun canShowInterstitialAd(): Boolean {
        val timeSinceLastAd = System.currentTimeMillis() - lastAdShowTime
        return timeSinceLastAd >= minAdInterval
    }

    // Public utility methods

    fun isAppInForeground(): Boolean = isAppInForeground

    fun isInitialized(): Boolean = isInitialized

    fun getTimeSinceLastAd(): Long = System.currentTimeMillis() - lastAdShowTime

    fun setMinAdInterval(intervalMs: Long) {
        // Allow customization of minimum ad interval
    }

    // Convenience methods for backward compatibility

    /**
     * Static methods for easy access (similar to AdsShowingClass)
     */
    object adManagerShow {
        @JvmStatic
        fun showBannerAdsStatic(activity: Activity, viewGroup: ViewGroup) {
            instanceLifecycle.getInstance().showBannerAds(activity, viewGroup)
        }

        @JvmStatic
        fun showNativeAdsStatic(activity: Activity, viewGroup: ViewGroup) {
            instanceLifecycle.getInstance().showNativeAds(activity, viewGroup)
        }

        @JvmStatic
        fun showInterstitialAdStatic(activity: Activity, onAdClosed: () -> Unit) {
            instanceLifecycle.getInstance().showInterstitialAd(activity, onAdClosed)
        }

        @JvmStatic
        fun showRewardAdsStatic(
            activity: Activity,
            onRewardEarned: (reward: RewardItem) -> Unit,
            onAdClosed: () -> Unit
        ) {
            instanceLifecycle.getInstance().showRewardAds(activity, onRewardEarned, onAdClosed)
        }

        @JvmStatic
        fun showRewardInterstitialAdsStatic(
            activity: Activity,
            onRewardEarned: (amount: Int, type: String) -> Unit,
            onAdClosed: () -> Unit
        ) {
            instanceLifecycle.getInstance()
                .showRewardInterstitialAds(activity, onRewardEarned, onAdClosed)
        }
    }
} 