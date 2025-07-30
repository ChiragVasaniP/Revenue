package com.chirag.googleads.adsUtil.banner


/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.chirag.googleads.BuildConfig
import com.chirag.googleads.consent.AdConsentUtil
import com.chirag.googleads.localcache.LocalAdPrefHelper
import com.chirag.googleads.util.getCurrentActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
private const val TAG = "BannerAdsUtil"
private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
    adWidthPx: Int = 360,
    alignment: Alignment = Alignment.Center,
) {
    val context = LocalContext.current
    getCurrentActivity()?.let { if (!canShowAds(it)) return }

    val isInPreview = LocalInspectionMode.current
    val effectiveAdUnitId = if (BuildConfig.DEBUG || LocalAdPrefHelper.getIsDebugAds(activity = getCurrentActivity()?:return)) TEST_BANNER_AD_UNIT_ID else LocalAdPrefHelper.getBannerAdId()

    // Initialize MobileAds in a LaunchedEffect
    LaunchedEffect(Unit) {
        if (!isInPreview) {
            withContext(Dispatchers.Main) {
                MobileAds.initialize(context) {}
            }
        }
    }
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                AdView(context).apply {
                    this.adUnitId = effectiveAdUnitId
                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidthPx))

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d(TAG, "Banner ad was loaded.")
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e(TAG, "Banner ad failed to load: ${error.message}")
                        }

                        override fun onAdImpression() {
                            Log.d(TAG, "Banner ad recorded an impression.")
                        }

                        override fun onAdClicked() {
                            Log.d(TAG, "Banner ad was clicked.")
                        }  }
                }
            },
            update = { adView ->
                if (!isInPreview) {
                    // Load ad directly without LaunchedEffect
                    adView.loadAd(AdRequest.Builder().build())
                }
            }
        )
    }



}

@Composable
fun BannerScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        BannerAd()
    }
}

private fun canShowAds(activity: Activity): Boolean {
    return LocalAdPrefHelper.isAdsEnabled(activity = activity) && AdConsentUtil.canRequestAds(activity)
}

@Preview
@Composable
private fun BannerScreenPreview() {
    Surface(color = MaterialTheme.colorScheme.background) {
        BannerScreen()
    }
}
