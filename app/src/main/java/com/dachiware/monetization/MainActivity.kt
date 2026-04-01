package com.dachiware.monetization

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.chirag.googleads.AdsShowingClass
import com.chirag.googleads.LifecycleAwareAdsDemoActivity
import com.chirag.googleads.consent.AdConsentUtil
//import com.chirag.googleads.event.FirebaseInitializer
import com.chirag.googleads.localcache.CLICK_TYPE
import com.chirag.googleads.localcache.LocalAdPrefHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FirebaseInitializer.init(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navigateToMain()
    }

    private fun navigateToMain() {
        setAdsLocalData()

        AdConsentUtil.gatherConsent(this@MainActivity) { aBoolean: Boolean? ->
            if (AdConsentUtil.canRequestAds(this@MainActivity)) {
                AdsShowingClass.loadOpenAppAds(this@MainActivity)
                Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                    override fun run() {
                        AdConsentUtil.showAdIfAvailableAndThen(this@MainActivity) {
                            startActivity(
                                Intent(
                                    this@MainActivity,
                                    LifecycleAwareAdsDemoActivity::class.java
                                )
                            )
                            finish()
                        }
                    }
                }, 15000)
            }
        }

    }

    private fun setAdsLocalData() {
        LocalAdPrefHelper.setAdsEnabled(true)
        LocalAdPrefHelper.enableAdsLogging(true)
//        LocalAdPrefHelper.setBannerAdId("ca-app-pub-3940256099942544/9214589741")
//        LocalAdPrefHelper.setInterstitialAdId("ca-app-pub-3940256099942544/1033173712")
//        LocalAdPrefHelper.setNativeAdId("ca-app-pub-3940256099942544/2247696110")
//        LocalAdPrefHelper.setRewardedAdId("ca-app-pub-3940256099942544/5224354917")
//        LocalAdPrefHelper.setRewardedInterstitialAdId("ca-app-pub-3940256099942544/5354046379")
//        LocalAdPrefHelper.setAppOpenAdId("ca-app-pub-3940256099942544/9257395921")
        LocalAdPrefHelper.setPlayConsoleAppVersionCode(0)
        LocalAdPrefHelper.setOnClickCounterAd(1)
        LocalAdPrefHelper.setClickAdType(CLICK_TYPE.INTERSTITIAL.nameValue)

//        Enable Debug Ad's
        LocalAdPrefHelper.setIsDebugAds(true)
        LocalAdPrefHelper.setDebugAppVersion(BuildConfig.VERSION_CODE.toLong())
        LocalAdPrefHelper.setTestDeviceIds("94E478E0C133848F5605B6D42EE2640D")
    }
}