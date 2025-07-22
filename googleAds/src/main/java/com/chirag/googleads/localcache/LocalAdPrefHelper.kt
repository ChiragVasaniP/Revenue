package com.chirag.googleads.localcache

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.util.LogWriter
import com.chirag.googleads.R


object LocalAdPrefHelper {

    // Google Ad's Preference Keys
    private const val KEY_GOOGLE_BANNER_AD_ID = "google_banner_ad_id"
    private const val KEY_GOOGLE_INTERSTITIAL_AD_ID = "google_interstitial_ad_id"
    private const val KEY_GOOGLE_NATIVE_AD_ID = "google_native_ad_id"
    private const val KEY_GOOGLE_REWARDED_AD_ID = "google_rewarded_ad_id"
    private const val KEY_GOOGLE_REWARDED_INTERSTITIAL_AD_ID = "google_rewarded_Interstitial_ad_id"
    private const val KEY_GOOGLE_APP_OPEN_AD_ID = "google_app_open_ad_id"

    //Ads general Preference Keys
    private const val KEY_IS_ADS_ENABLED = "is_ads_enabled"
    private const val KEY_PLAY_CONSOLE_APP_VERSION_CODE = "play_console_app_version_code"
    private const val KEY_AD_CLICK_COUNTER = "ad_click_counter"
    private const val KEY_CLICK_AD_TYPE = "ad_click_type"
    private const val KEY_IS_MUTE_AD = "is_mute_ad"

    fun ifPrefManagerCrash(context: Context) {
        PreferenceManager.init(context)
    }

    // Banner Ad ID
    fun setBannerAdId(adId: String) {
        PreferenceManager.putString(KEY_GOOGLE_BANNER_AD_ID, adId)
    }

    internal fun getBannerAdId(default: String = ""): String {
        return PreferenceManager.getString(KEY_GOOGLE_BANNER_AD_ID, default)
    }

    // Interstitial Ad ID
    fun setInterstitialAdId(adId: String) {
        PreferenceManager.putString(KEY_GOOGLE_INTERSTITIAL_AD_ID, adId)
    }

    internal fun getInterstitialAdId(default: String = ""): String {
        return PreferenceManager.getString(KEY_GOOGLE_INTERSTITIAL_AD_ID, default)
    }

    // Native Ad ID
    fun setNativeAdId(adId: String) {
        PreferenceManager.putString(KEY_GOOGLE_NATIVE_AD_ID, adId)
    }

    internal fun getNativeAdId(default: String = ""): String {
        return PreferenceManager.getString(KEY_GOOGLE_NATIVE_AD_ID, default)
    }

    // Rewarded Ad ID
    fun setRewardedAdId(adId: String) {
        PreferenceManager.putString(KEY_GOOGLE_REWARDED_AD_ID, adId)
    }

    internal fun getRewardedAdId(default: String = ""): String {
        return PreferenceManager.getString(KEY_GOOGLE_REWARDED_AD_ID, default)
    }

    // Rewarded Interstitial Ad ID
    fun setRewardedInterstitialAdId(adId: String) {
        PreferenceManager.putString(KEY_GOOGLE_REWARDED_INTERSTITIAL_AD_ID, adId)
    }

    internal fun getRewardedInterstitialAdId(default: String = ""): String {
        return PreferenceManager.getString(KEY_GOOGLE_REWARDED_INTERSTITIAL_AD_ID, default)
    }

    // App open Ad ID
    fun setAppOpenAdId(adId: String) {
        PreferenceManager.putString(KEY_GOOGLE_APP_OPEN_AD_ID, adId)
    }

    internal fun getAppOpenAdId(default: String = ""): String {
        return PreferenceManager.getString(KEY_GOOGLE_APP_OPEN_AD_ID, default)
    }

    // Ad Enable/Disable flag
    fun setAdsEnabled(enabled: Boolean) {
        PreferenceManager.putBoolean(KEY_IS_ADS_ENABLED, enabled)
    }

    internal fun isAdsEnabled(default: Boolean = true, activity: Activity): Boolean {
        // Return true if activity is from the module
        if (isActivityFromModule(activity)) {
            return true
        }
        
        return PreferenceManager.getBoolean(
            KEY_IS_ADS_ENABLED,
            default
        ) && PreferenceManager.getLong(KEY_PLAY_CONSOLE_APP_VERSION_CODE, 0) != getAppVersionCode(
            activity
        )
    }
    
    /**
     * Check if the activity is from the Google Ads module
     */
    private fun isActivityFromModule(activity: Activity): Boolean {
        val modulePackageName = "com.chirag.googleads"
        return activity.javaClass.`package`?.name?.startsWith(modulePackageName) == true ||
               activity.javaClass.name.startsWith(modulePackageName)
    }


    private fun getAppVersionCode(activity: Activity): Long {
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.packageManager.getPackageInfo(activity.packageName, 0).longVersionCode
        } else {
            activity.packageManager.getPackageInfo(activity.packageName, 0).versionCode.toLong()
        }
        return versionCode
    }

    fun setPlayConsoleAppVersionCode(longVersionCode: Long) {
        PreferenceManager.putLong(KEY_PLAY_CONSOLE_APP_VERSION_CODE, longVersionCode)
    }

    internal fun getPlayConsoleAppVersionCode(default: Long = 0): Long {
        return PreferenceManager.getLong(KEY_PLAY_CONSOLE_APP_VERSION_CODE, default)
    }


    fun setOnClickCounterAd(clickCounter: Long) {
        PreferenceManager.putLong(KEY_AD_CLICK_COUNTER, clickCounter)
    }

    internal fun getOnClickCounterAd(default: Long = 0): Long {
        return PreferenceManager.getLong(KEY_AD_CLICK_COUNTER, default)
    }

    fun setClickAdType(clickAdType: String) {
        PreferenceManager.putString(KEY_CLICK_AD_TYPE, clickAdType)
    }

    internal fun getClickAdType(default: String = CLICK_TYPE.NOUN.nameValue): String {
        return PreferenceManager.getString(KEY_CLICK_AD_TYPE, default)
    }

    // Clear all ad keys
    fun clearAdPreferences() {
        PreferenceManager.remove(KEY_GOOGLE_BANNER_AD_ID)
        PreferenceManager.remove(KEY_GOOGLE_INTERSTITIAL_AD_ID)
        PreferenceManager.remove(KEY_GOOGLE_NATIVE_AD_ID)
        PreferenceManager.remove(KEY_GOOGLE_REWARDED_AD_ID)
        PreferenceManager.remove(KEY_IS_ADS_ENABLED)
    }
}

enum class CLICK_TYPE(val nameValue: String) {
    REWARD("reward"),
    INTERSTITIAL("interstitial"),
    APP_OPEN("app_open"),
    NOUN("noun")
}
