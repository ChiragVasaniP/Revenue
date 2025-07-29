package com.chirag.googleads.util


import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import com.chirag.googleads.AdsShowingClass
import com.chirag.googleads.localcache.CLICK_TYPE
import com.chirag.googleads.localcache.LocalAdPrefHelper

abstract class OnOneOffClickListener(val activity: Activity) : View.OnClickListener {

    companion object {
        private const val MIN_CLICK_INTERVAL = 1000L
        var isViewClicked = false
        var interStillCounter =0L
    }

    private var mLastClickTime: Long = 0

    abstract fun onSingleClick(v: View)

    override fun onClick(view: View) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime

        if (elapsedTime <= MIN_CLICK_INTERVAL) return
        if (!isViewClicked) {
            isViewClicked = true
            startTimer()
        } else {
            return
        }

        val context = view.context
        AdProgressManager.showAdLoadingDialog(activity)
        if (LocalAdPrefHelper.getOnClickCounterAd(5) == interStillCounter) {
            interStillCounter = 1

            when (LocalAdPrefHelper.getClickAdType(CLICK_TYPE.INTERSTITIAL.nameValue)) {
                CLICK_TYPE.REWARD.nameValue -> {
                    AdsShowingClass.showRewardAds(activity = activity,{}){
                        AdProgressManager.dismissDialog()
                        onSingleClick(view)
                    }
                }

                CLICK_TYPE.INTERSTITIAL.nameValue -> {
                    AdsShowingClass.showInterstitialAd(activity = activity){
                        AdProgressManager.dismissDialog()
                        onSingleClick(view)
                    }

                }
          /*      CLICK_TYPE.APP_OPEN.nameValue->{

                }*/
                else -> {
                    AdProgressManager.dismissDialog()
                    onSingleClick(view)
                }
            }
        } else {
            interStillCounter++
            AdProgressManager.dismissDialog()
            onSingleClick(view)
        }
    }

    private fun startTimer() {
        Handler(Looper.getMainLooper()).postDelayed({
            isViewClicked = false
        }, 600)
    }
}
