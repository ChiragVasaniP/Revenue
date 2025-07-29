package com.chirag.googleads.adsUtil.banner

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import com.chirag.googleads.BuildConfig
import com.chirag.googleads.localcache.LocalAdPrefHelper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal object BannerAdsUtil {
    private const val TAG = "AdShowingUtil"
    private const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"

    /**
     * Load and show a banner ad into the given container.
     *
     * @param activity The activity context to use.
     * @param container The ViewGroup where the AdView will be added.
     * @param adUnitId Your AdMob banner ad unit ID.
     * @param testDeviceId (Optional) Add test device for debugging.
     * @param adWidthPx (Optional) Width of the ad in pixels. Defaults to 360.
     */
    fun showBannerAd(
        activity: Activity,
        container: ViewGroup,
        adWidthPx: Int = 360/*container.width ?: 360*/
    ) {
        // Configure test device if needed
//        MobileAds.setRequestConfiguration(
//            RequestConfiguration.Builder()
//                .setTestDeviceIds(listOf(testDeviceId))
//                .build()
//        )

        // Initialize SDK only once
        CoroutineScope(Dispatchers.Main).launch {
            MobileAds.initialize(activity) {}

            // Run UI-related actions on the main thread
            activity.runOnUiThread {
                val adView = AdView(activity).apply {
                    adUnitId = BANNER_AD_UNIT_ID.takeIf { BuildConfig.DEBUG }?: LocalAdPrefHelper.getBannerAdId()
                    setAdSize(
                        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                            activity, adWidthPx
                        )
                    )
                }

                // Replace old ads if any
                container.removeAllViews()
                container.addView(adView)

                val adRequest = AdRequest.Builder().build()
                adView.loadAd(adRequest)

                Log.d(TAG, "Banner Ad loaded in container.")
            }
        }
    }
}