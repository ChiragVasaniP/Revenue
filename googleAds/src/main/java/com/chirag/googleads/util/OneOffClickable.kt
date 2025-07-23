package com.chirag.googleads.util

import android.app.Activity
import android.content.ContextWrapper
import android.os.SystemClock
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.chirag.googleads.AdsShowingClass
import com.chirag.googleads.localcache.CLICK_TYPE
import com.chirag.googleads.localcache.LocalAdPrefHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
fun oneOffClickable(
    mActivity: Activity?,
    onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableStateOf(0L) }
    var interStillCounter by remember { mutableStateOf(1L) }
    var isViewClicked by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    return {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - lastClickTime

        // Early exit cases wrapped in conditional
        if (mActivity == null || elapsedTime <= 1000L || isViewClicked) {
            // Do nothing - this is equivalent to returning Unit
        } else {
            lastClickTime = currentClickTime
            isViewClicked = true

            coroutineScope.launch {
                delay(600)
                isViewClicked = false
            }

            val maxClick = LocalAdPrefHelper.getOnClickCounterAd(5)

            if (interStillCounter >= maxClick.toLong()) {
                interStillCounter = 1

                when (LocalAdPrefHelper.getClickAdType(CLICK_TYPE.INTERSTITIAL.nameValue)) {
                    CLICK_TYPE.REWARD.nameValue -> {
                        AdsShowingClass.showRewardAds(
                            activity = mActivity,
                            onRewardEarned = {},
                            onAdClosed = { onClick() }
                        )
                    }
                    CLICK_TYPE.INTERSTITIAL.nameValue -> {
                        AdsShowingClass.showInterstitialAd(
                            activity = mActivity,
                            onAdClosed = { onClick() }
                        )
                    }
                    else -> {
                        onClick()
                    }
                }
            } else {
                interStillCounter++
                onClick()
            }
        }
    }
}
@Preview
@Composable
fun MyScreen() {
    val safeClick = oneOffClickable(getCurrentActivity()) {
        Log.d("Click", "Actual click logic")
    }

    Button(onClick = safeClick) {
        Text("Click Me")
    }
}

@Composable
fun getCurrentActivity(): Activity? {
    val context = LocalContext.current
    return context as? Activity ?: (context as? ContextWrapper)?.baseContext as? Activity
}