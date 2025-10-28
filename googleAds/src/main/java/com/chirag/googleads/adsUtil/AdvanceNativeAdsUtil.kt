package com.chirag.googleads.adsUtil

import android.app.Activity
import com.chirag.googleads.util.Logger
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.chirag.googleads.BuildConfig
import com.chirag.googleads.databinding.AdUnifiedBinding
import com.chirag.googleads.localcache.LocalAdPrefHelper
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.VideoOptions

internal object AdvanceNativeAdsUtil {

    private const val TAG = "NativeAdUtil"
    private var currentNativeAd: NativeAd? = null
    private const val NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

    /**
     * Load and display a native ad in the specified container.
     *
     * @param activity The current activity.
     * @param adUnitId Your native ad unit ID.
     * @param container The ViewGroup where the ad should be added.
     * @param startMuted Whether the video should start muted.
     */
    fun loadAndShowNativeAd(
        activity: Activity,
        container: ViewGroup,
        startMuted: Boolean = true
    ) {
        val builder = AdLoader.Builder(activity, NATIVE_AD_UNIT_ID.takeIf { BuildConfig.DEBUG  || LocalAdPrefHelper.getIsDebugAds(activity = activity)}?: LocalAdPrefHelper.getNativeAdId(NATIVE_AD_UNIT_ID))

        builder.forNativeAd { nativeAd ->
            if (activity.isFinishing || activity.isDestroyed) {
                nativeAd.destroy()
                return@forNativeAd
            }
            nativeAd.setOnPaidEventListener {adValue->
                Logger.d(TAG, "forNativeAd onPaidEventListener: ${adValue.precisionType} ${adValue.valueMicros} ${adValue.currencyCode}")
            }

            // Clean up previous ad if needed
            currentNativeAd?.destroy()
            currentNativeAd = nativeAd



            // Inflate layout and populate ad
            val adBinding = AdUnifiedBinding.inflate(LayoutInflater.from(activity))
            populateNativeAdView(nativeAd, adBinding)
            container.removeAllViews()
            container.addView(adBinding.root)
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(startMuted)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                Logger.e(TAG, "Native ad failed to load: ${error.message}")
                Toast.makeText(activity, "Failed to load native ad.", Toast.LENGTH_SHORT).show()
            }
        })

        val adLoader = builder.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    /**
     * Populates a NativeAdView with assets from the NativeAd.
     */
    private fun populateNativeAdView(nativeAd: NativeAd, binding: AdUnifiedBinding) {
        val adView = binding.root

        adView.mediaView = binding.adMedia
        adView.headlineView = binding.adHeadline
        adView.bodyView = binding.adBody
        adView.callToActionView = binding.adCallToAction
        adView.iconView = binding.adAppIcon
        adView.priceView = binding.adPrice
        adView.starRatingView = binding.adStars
        adView.storeView = binding.adStore
        adView.advertiserView = binding.adAdvertiser

        binding.adHeadline.text = nativeAd.headline
        nativeAd.mediaContent?.let {
            binding.adMedia.setMediaContent(it)
        }

        binding.adBody.apply {
            text = nativeAd.body
            visibility = if (nativeAd.body == null) ViewGroup.GONE else ViewGroup.VISIBLE
        }

        binding.adCallToAction.apply {
            text = nativeAd.callToAction
            visibility = if (nativeAd.callToAction == null) ViewGroup.GONE else ViewGroup.VISIBLE
        }

        binding.adAppIcon.apply {
            setImageDrawable(nativeAd.icon?.drawable)
            visibility = if (nativeAd.icon == null) ViewGroup.GONE else ViewGroup.VISIBLE
        }

        binding.adPrice.apply {
            text = nativeAd.price
            visibility = if (nativeAd.price == null) ViewGroup.GONE else ViewGroup.VISIBLE
        }

        binding.adStars.apply {
            rating = nativeAd.starRating?.toFloat() ?: 0f
            visibility = if (nativeAd.starRating == null) ViewGroup.GONE else ViewGroup.VISIBLE
        }

        binding.adStore.apply {
            text = nativeAd.store
            visibility = if (nativeAd.store == null) ViewGroup.GONE else ViewGroup.VISIBLE
        }

        binding.adAdvertiser.apply {
            text = nativeAd.advertiser
            visibility = if (nativeAd.advertiser == null) ViewGroup.GONE else ViewGroup.VISIBLE
        }

        adView.setNativeAd(nativeAd)

        nativeAd.mediaContent?.videoController?.let { vc ->
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    Logger.d(TAG, "Video playback has ended.")
                    super.onVideoEnd()
                }
            }
        }
    }

    /**
     * Call this from activity `onDestroy()` to avoid memory leaks.
     */
    fun destroy() {
        currentNativeAd?.destroy()
        currentNativeAd = null
    }
}
