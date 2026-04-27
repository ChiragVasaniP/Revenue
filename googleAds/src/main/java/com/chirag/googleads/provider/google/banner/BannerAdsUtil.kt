package com.chirag.googleads.provider.google.banner

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import com.chirag.googleads.BuildConfig
import com.chirag.googleads.localcache.LocalAdPrefHelper
import com.chirag.googleads.util.Logger
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

internal object BannerAdsUtil {
    private const val TAG = "AdShowingUtil"
    private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"

    /**
     * Load and show a banner ad into the given container.
     *
     * @param activity The activity context to use.
     * @param container The ViewGroup where the AdView will be added.
     * @param adWidthPx (Optional) Width of the ad in pixels. Defaults to 360.
     * @param isNeedToUseCollapsible (Optional) Whether to use collapsible banner.
     */
    fun showBannerAd(
        activity: Activity,
        container: ViewGroup,
        adWidthPx: Int = 360,
        isNeedToUseCollapsible: Boolean = false
    ) {
        // Run UI-related actions on the main thread
        activity.runOnUiThread {
            val adView = AdView(activity).apply {
                adUnitId = BANNER_AD_UNIT_ID.takeIf {
                    BuildConfig.DEBUG || LocalAdPrefHelper.getIsDebugAds(activity = activity)
                } ?: LocalAdPrefHelper.getBannerAdId(BANNER_AD_UNIT_ID)
                
                setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        activity, adWidthPx
                    )
                )
            }

            // Replace old ads if any
            container.removeAllViews()
            container.addView(adView)

            val adRequestBuilder = AdRequest.Builder()
            if (isNeedToUseCollapsible) {
                val extras = Bundle()
                extras.putString("collapsible", "bottom")
                adRequestBuilder.addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            }
            
            val adRequest = adRequestBuilder.build()
            adView.loadAd(adRequest)
        }

        Logger.d(TAG, "Banner Ad requested in container.")
    }
}
