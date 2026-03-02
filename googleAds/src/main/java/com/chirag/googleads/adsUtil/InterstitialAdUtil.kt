package com.chirag.googleads.adsUtil


import android.app.Activity
import com.chirag.googleads.event.Logger
import android.widget.Toast
import com.chirag.googleads.BuildConfig
import com.chirag.googleads.localcache.LocalAdPrefHelper
//import com.chirag.googleads.util.AdProgressManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Utility object to handle Interstitial Ad loading and displaying.
 */
internal object InterstitialAdUtil {
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val TAG = "InterstitialAdUtil"
    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false

    /**
     * Loads a new interstitial ad.
     *
     * @param activity The activity context.
     * @param adUnitId Your interstitial ad unit ID.
     * @param onLoaded Optional callback when ad is loaded successfully.
     */
    fun loadAd(
        activity: Activity,
        onLoaded: (() -> Unit)? = null
    ) {
        if (isAdLoading || interstitialAd != null){
            onLoaded?.invoke()
            return
        }

        isAdLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            INTERSTITIAL_AD_UNIT_ID.takeIf { BuildConfig.DEBUG || LocalAdPrefHelper.getIsDebugAds(activity = activity) }?:LocalAdPrefHelper.getInterstitialAdId(INTERSTITIAL_AD_UNIT_ID),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoading = false
                    ad.onPaidEventListener = OnPaidEventListener {adValue ->
                        Logger.d(TAG, "Interstitial onPaidEventListener: ${adValue.precisionType} ${adValue.valueMicros} ${adValue.currencyCode}")
                    }
                    Logger.d(TAG, "Interstitial ad loaded")
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Logger.e(TAG, "Interstitial ad failed to load: ${adError.message}")
                    interstitialAd = null
                    isAdLoading = false
                    if (BuildConfig.DEBUG) Logger.makeTextToast(
                        activity,
                        "Ad failed to load: ${adError.message}",
                        Toast.LENGTH_SHORT
                    )
                }
            }
        )
    }

    /**
     * Shows the interstitial ad if it's ready.
     *
     * @param activity The activity context.
     * @param onAdClosed Callback to run after the ad is dismissed or failed to show.
     */
    fun showAdIfAvailable(activity: Activity, onAdClosed: () -> Unit) {
        val ad = interstitialAd

        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Logger.d(TAG, "Interstitial ad dismissed.")
                    interstitialAd = null
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Logger.w(TAG, "Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    onAdClosed()
                }

                override fun onAdShowedFullScreenContent() {
                    Logger.d(TAG, "Interstitial ad showed.")
                }

                override fun onAdClicked() {
                    Logger.d(TAG, "Interstitial ad clicked.")
                }

                override fun onAdImpression() {
                    Logger.d(TAG, "Interstitial ad impression recorded.")
                }
            }

            ad.show(activity)
        } else {
            Logger.d(TAG, "Interstitial ad not ready.")
            onAdClosed()
        }
    }

    /**
     * Clears the cached ad reference (optional use).
     */
    fun clearAd() {
        interstitialAd = null
    }


    /**
     * Loads and immediately shows an interstitial ad once loaded.
     *
     * @param activity The activity context.
     * @param adUnitId Your interstitial ad unit ID.
     * @param onAdClosed Callback when the ad is dismissed or fails to show.
     */
    fun loadAndShowAd(activity: Activity,  onAdClosed: () -> Unit) {
        if (isAdLoading) {
//            onAdClosed.invoke()
            Logger.d(TAG, "Ad is already loading.")
            return
        }

        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
//        AdProgressManager.showAdLoadingDialog(activity)
        InterstitialAd.load(
            activity,
            INTERSTITIAL_AD_UNIT_ID.takeIf { BuildConfig.DEBUG }?:LocalAdPrefHelper.getInterstitialAdId(
                INTERSTITIAL_AD_UNIT_ID),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Logger.d(TAG, "Interstitial ad loaded")
                    isAdLoading = false
                    interstitialAd = ad

                    ad.onPaidEventListener = OnPaidEventListener {adValue ->
                        Logger.d(TAG, "Interstitial onPaidEventListener: ${adValue.precisionType} ${adValue.valueMicros} ${adValue.currencyCode}")
                    }

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Logger.d(TAG, "Ad dismissed.")
                            interstitialAd = null
//                            AdProgressManager.dismissDialog()
                            onAdClosed()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Logger.w(TAG, "Ad failed to show: ${adError.message}")
                            interstitialAd = null
//                            AdProgressManager.dismissDialog()
                            onAdClosed()
                        }

                        override fun onAdShowedFullScreenContent() {
                            Logger.d(TAG, "Ad showed.")
                        }

                        override fun onAdImpression() {
                            Logger.d(TAG, "Ad impression recorded.")
                        }

                        override fun onAdClicked() {
                            Logger.d(TAG, "Ad clicked.")
                        }
                    }

                    ad.show(activity)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Logger.e(TAG, "Failed to load interstitial ad: ${adError.message}")
                    interstitialAd = null
                    isAdLoading = false
                    if (BuildConfig.DEBUG) Logger.makeTextToast(activity, "Ad failed: ${adError.message}", Toast.LENGTH_SHORT)
//                    AdProgressManager.dismissDialog()
                    onAdClosed()
                }
            }
        )
    }
}
