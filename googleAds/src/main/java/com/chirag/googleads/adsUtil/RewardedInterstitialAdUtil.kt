package com.chirag.googleads.adsUtil


import android.app.Activity
import com.chirag.googleads.util.Logger
import android.widget.Toast
import com.chirag.googleads.BuildConfig
import com.chirag.googleads.localcache.LocalAdPrefHelper
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

/**
 * Utility class to load and show rewarded interstitial ads in a single call.
 */
internal object RewardedInterstitialAdUtil {
    private const val REWARD_INT_AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379"
    private const val TAG = "RewardedInterstitialAdUtil"
    private var isAdLoading = false

    /**
     * Loads and shows a Rewarded Interstitial Ad when ready.
     *
     * @param activity The current activity context.
     * @param adUnitId The AdMob rewarded interstitial ad unit ID.
     * @param onRewardEarned Callback with the reward item amount and type.
     * @param onAdClosed Called after ad is dismissed or fails to show.
     */
    fun loadAndShowAd(
        activity: Activity,
        onRewardEarned: (amount: Int, type: String) -> Unit,
        onAdClosed: () -> Unit
    ) {
        if (isAdLoading) {
            Logger.d(TAG, "Ad is already loading.")
            return
        }

        isAdLoading = true

        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
            activity,
            REWARD_INT_AD_UNIT_ID.takeIf { BuildConfig.DEBUG || LocalAdPrefHelper.getIsDebugAds(activity = activity)}?:LocalAdPrefHelper.getRewardedInterstitialAdId(REWARD_INT_AD_UNIT_ID),
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    Logger.d(TAG, "Rewarded Interstitial Ad Loaded.")
                    isAdLoading = false

                    ad.onPaidEventListener = OnPaidEventListener {adValue ->
                        Logger.d(TAG, "RewardedInterstitialAd onPaidEventListener: ${adValue.precisionType} ${adValue.valueMicros} ${adValue.currencyCode}")
                    }

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Logger.d(TAG, "Ad dismissed.")
                            onAdClosed()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Logger.e(TAG, "Ad failed to show: ${adError.message}")
                            onAdClosed()
                        }

                        override fun onAdShowedFullScreenContent() {
                            Logger.d(TAG, "Ad showed fullscreen content.")
                        }

                        override fun onAdClicked() {
                            Logger.d(TAG, "Ad clicked.")
                        }

                        override fun onAdImpression() {
                            Logger.d(TAG, "Ad impression recorded.")
                        }
                    }

                    ad.show(activity) { rewardItem ->
                        Logger.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                        onRewardEarned(rewardItem.amount, rewardItem.type)
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Logger.e(TAG, "Ad failed to load: ${adError.message}")
                    isAdLoading = false
                    Toast.makeText(activity, "Ad failed to load.", Toast.LENGTH_SHORT).show()
                    onAdClosed()
                }
            }
        )
    }
}
