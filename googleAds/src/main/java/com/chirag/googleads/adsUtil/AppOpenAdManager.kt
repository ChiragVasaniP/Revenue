package com.chirag.googleads.adsUtil

import android.app.Activity
import android.content.Context
import com.chirag.googleads.event.Logger
import android.widget.Toast
import com.chirag.googleads.manager.AdsShowingClass
import com.chirag.googleads.BuildConfig
import com.chirag.googleads.consent.AdConsentUtil
import com.chirag.googleads.consent.GoogleMobileAdsConsentManager
import com.chirag.googleads.localcache.LocalAdPrefHelper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

// This is an ad unit ID for a test ad. Replace with your own app open ad unit ID.
private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
private const val LOG_TAG = "AppOpenAdManager"

/**
 * Interface definition for a callback to be invoked when an app open ad is complete (i.e.
 * dismissed or fails to show).
 */
interface OnShowAdCompleteListener {
    fun onShowAdComplete()
}

/** Inner class that loads and shows app open ads. */
internal class AppOpenAdManager(applicationContext: Context) {

    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager =
        GoogleMobileAdsConsentManager.getInstance(applicationContext)
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private var loadTime: Long = 0



    /**
     * Load an ad.
     *
     * @param context the context of the activity that loads the ad
     */
    fun loadAd(context: Activity) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            AD_UNIT_ID.takeIf { BuildConfig.DEBUG || LocalAdPrefHelper.getIsDebugAds(activity = context) }?:LocalAdPrefHelper.getAppOpenAdId(AD_UNIT_ID),
            request,
            object : AppOpenAdLoadCallback() {
                /**
                 * Called when an app open ad has loaded.
                 *
                 * @param ad the loaded app open ad.
                 */
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad

                    ad.onPaidEventListener = OnPaidEventListener {adValue ->
                        Logger.d(LOG_TAG, "AppOpen onPaidEventListener: ${adValue.precisionType} ${adValue.valueMicros} ${adValue.currencyCode}")
                    }

                    isLoadingAd = false
                    loadTime = Date().time
                    Logger.d(LOG_TAG, "onAdLoaded.")
                    if (BuildConfig.DEBUG) Logger.makeTextToast(context, "onAdLoaded", Toast.LENGTH_SHORT)
                }

                /**
                 * Called when an app open ad has failed to load.
                 *
                 * @param loadAdError the error.
                 */
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Logger.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
                    if (BuildConfig.DEBUG) Logger.makeTextToast(context, "onAdFailedToLoad", Toast.LENGTH_SHORT)
                }
            },
        )
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     */
    fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(
            activity,
            object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                    // Empty because the user will go back to the activity that shows the ad.
                }
            },
        )
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Logger.d(LOG_TAG, "The app open ad is already showing.")
            return
        }

        if (AdsShowingClass.canShowAds(activity).not()){
            Logger.d(LOG_TAG, "The app open ad is not ready yet Or condition.")
            onShowAdCompleteListener.onShowAdComplete()
            return
        }

        // If the app open ad is not available yet, invoke the callback.
        if (!isAdAvailable()) {
            Logger.d(LOG_TAG, "The app open ad is not ready yet.")
            onShowAdCompleteListener.onShowAdComplete()
            if (googleMobileAdsConsentManager.canRequestAds) {
                loadAd(activity)
            }
            return
        }

        Logger.d(LOG_TAG, "Will show ad.")

        appOpenAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                /** Called when full screen content is dismissed. */
                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
                    Logger.d(LOG_TAG, "onAdDismissedFullScreenContent.")
                    if (BuildConfig.DEBUG) Logger.makeTextToast(activity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)

                    onShowAdCompleteListener.onShowAdComplete()
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        loadAd(activity)
                    }
                }

                /** Called when fullscreen content failed to show. */
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    Logger.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                    if (BuildConfig.DEBUG) Logger.makeTextToast(activity, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)

                    onShowAdCompleteListener.onShowAdComplete()
                    if (googleMobileAdsConsentManager.canRequestAds) {
                        loadAd(activity)
                    }
                }

                /** Called when fullscreen content is shown. */
                override fun onAdShowedFullScreenContent() {
                    Logger.d(LOG_TAG, "onAdShowedFullScreenContent.")
                    if (BuildConfig.DEBUG) Logger.makeTextToast(activity, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
                }
            }
        isShowingAd = true
        appOpenAd?.show(activity)
    }
}