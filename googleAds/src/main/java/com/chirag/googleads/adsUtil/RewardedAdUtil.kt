package com.chirag.googleads.adsUtil


import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.chirag.googleads.BuildConfig
import com.chirag.googleads.localcache.LocalAdPrefHelper
//import com.chirag.googleads.util.AdProgressManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * Utility class to simplify loading and showing Rewarded Video Ads.
 */
internal object RewardedAdUtil {
    private const val REWARD_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    private const val TAG = "RewardedAdUtil"
    private var isLoading = false

    /**
     * Loads and immediately shows a rewarded video ad.
     *
     * @param activity Activity context.
     * @param adUnitId Your rewarded ad unit ID.
     * @param onRewardEarned Called when the user earns a reward.
     * @param onAdClosed Called when the ad is dismissed or fails to show.
     */
    fun loadAndShowAd(
        activity: Activity,
        onRewardEarned: (reward: RewardItem) -> Unit,
        onAdClosed: () -> Unit
    ) {
        if (isLoading) {
            Log.d(TAG, "Rewarded ad is already loading.")
            return
        }

        isLoading = true
        val adRequest = AdRequest.Builder().build()
//        AdProgressManager.showAdLoadingDialog(activity)

        RewardedAd.load(
            activity,
            REWARD_AD_UNIT_ID.takeIf { BuildConfig.DEBUG || LocalAdPrefHelper.getIsDebugAds(activity = activity) }?:LocalAdPrefHelper.getRewardedAdId(REWARD_AD_UNIT_ID),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Rewarded ad loaded.")
                    isLoading = false

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Ad was dismissed.")
//                            AdProgressManager.dismissDialog()
                            onAdClosed()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Ad failed to show: ${adError.message}")
//                            AdProgressManager.dismissDialog()
                            onAdClosed()
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Ad is showing.")
                        }

                        override fun onAdClicked() {
                            Log.d(TAG, "Ad clicked.")
                        }

                        override fun onAdImpression() {
                            Log.d(TAG, "Ad impression recorded.")
                        }
                    }

                    ad.show(
                        activity,
                        OnUserEarnedRewardListener { rewardItem ->
                            Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                            onRewardEarned(rewardItem)
                        }
                    )
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Ad failed to load: ${adError.message}")
                    isLoading = false
                    if (BuildConfig.DEBUG) Toast.makeText(activity, "Rewarded ad failed to load.", Toast.LENGTH_SHORT).show()
//                    AdProgressManager.dismissDialog()
                    onAdClosed()
                }
            }
        )
    }
}
