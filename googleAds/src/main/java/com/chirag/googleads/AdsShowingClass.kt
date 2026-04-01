package com.chirag.googleads

import android.app.Activity
import android.app.Application
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.chirag.googleads.adsUtil.*
import com.chirag.googleads.adsUtil.banner.BannerAdsUtil
import com.chirag.googleads.base.ApplicationLifecycleManagerOpenAds
import com.chirag.googleads.consent.AdConsentUtil
import com.chirag.googleads.consent.AdConsentUtil.canRequestAds
import com.chirag.googleads.localcache.LocalAdPrefHelper
import com.google.android.gms.ads.rewarded.RewardItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Keep
object AdsShowingClass {


    /**
     * Displays a banner ad inside the given [viewGroup].
     */
    fun showBannerAds(activity: Activity, viewGroup: ViewGroup) {
        if (!canShowAds(activity)) return
        BannerAdsUtil.showBannerAd(activity, container = viewGroup)
    }

    /**
     * Displays a native ad inside the given [viewGroup].
     */
    fun showNativeAds(activity: Activity, viewGroup: ViewGroup) {
        if (!canShowAds(activity)) return
        AdvanceNativeAdsUtil.loadAndShowNativeAd(activity, container = viewGroup)
    }

    /**
     * Displays an interstitial ad. Executes [onAdClosed] when the ad is closed.
     */
    fun showInterstitialAd(activity: Activity, onAdClosed: () -> Unit) {
        if (!canShowAds(activity)) {
            onAdClosed.invoke()
            return
        }
        InterstitialAdUtil.loadAndShowAd(activity, onAdClosed)
    }

    /**
     * Displays a rewarded ad. Calls [onRewardEarned] when the user earns a reward,
     * and [onAdClosed] when the ad is closed.
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
        RewardedAdUtil.loadAndShowAd(activity, onRewardEarned, onAdClosed)
    }

    /**
     * Displays a rewarded interstitial ad. Calls [onRewardEarned] with reward info
     * and [onAdClosed] when the ad is closed.
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
        RewardedInterstitialAdUtil.loadAndShowAd(activity, onRewardEarned, onAdClosed)
    }

    fun loadOpenAppAds(context: Activity) {
        // Initialize Mobile Ads SDK on a background thread
        val singleToneApplicationLifeCyel = ApplicationLifecycleManagerOpenAds.getInstance()
        if (canRequestAds(context)) {
            CoroutineScope(Dispatchers.Main).launch {
                // Load the App Open Ad on the main thread
                singleToneApplicationLifeCyel?.loadAd(context)
            }
        }

    }


    /**
     * Checks whether ads are enabled and can be requested.
     */
    internal fun canShowAds(activity: Activity): Boolean {
        return LocalAdPrefHelper.isAdsEnabled(activity = activity) && AdConsentUtil.canRequestAds(activity)
    }
}
