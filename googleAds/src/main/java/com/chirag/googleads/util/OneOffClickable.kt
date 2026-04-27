package com.chirag.googleads.util

import android.app.Activity
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.chirag.googleads.util.Logger
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.chirag.googleads.manager.AdsShowingClass
import com.chirag.googleads.consent.AdConsentUtil
import com.chirag.googleads.localcache.CLICK_TYPE
import com.chirag.googleads.localcache.LocalAdPrefHelper
import com.chirag.googleads.util.GlobalClickTracker.interStillCounter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object GlobalClickTracker {
    var interStillCounter: Long = 1L
}


@Composable
fun Modifier.oneAdClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier {
    return this.then(
        Modifier.clickable(
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = oneOffClickable(getCurrentActivity(), onClick)
        )
    )
}


@Composable
fun oneOffClickable(
    mActivity: Activity?,
    onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableStateOf(0L) }
//    var interStillCounter by remember { mutableStateOf(1L) }
    var isViewClicked by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LoadingProgressDialog(isLoading)

    return {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - lastClickTime

        // Early exit cases wrapped in conditional
       if (mActivity == null ){
           onClick.invoke()
       } else if (elapsedTime <= 1000L || isViewClicked) {
            // Do nothing - this is equivalent to returning Unit
        } else {
            lastClickTime = currentClickTime
            isViewClicked = true

            coroutineScope.launch {
                delay(600)
                isViewClicked = false
            }

            val maxClick = LocalAdPrefHelper.getOnClickCounterAd(5)
            Logger.d("ClickDebug", "interStillCounter: $interStillCounter, maxClick: $maxClick, elapsedTime: $elapsedTime, isViewClicked: $isViewClicked")

            if (interStillCounter >= maxClick.toLong()) {
                interStillCounter = 1
                isLoading =true
                when (LocalAdPrefHelper.getClickAdType(CLICK_TYPE.INTERSTITIAL.nameValue)) {
                    CLICK_TYPE.REWARD.nameValue -> {
                        AdsShowingClass.showRewardAds(
                            activity = mActivity,
                            onRewardEarned = {
                                isLoading =false
                            },
                            onAdClosed = {
                                isLoading =false
                                onClick()
                            }
                        )
                    }
                    CLICK_TYPE.INTERSTITIAL.nameValue -> {
                        AdsShowingClass.showInterstitialAd(
                            activity = mActivity,
                            onAdClosed = {
                                isLoading =false
                                onClick()
                            }
                        )
                    }
                    else -> {
                        Logger.d("Click", "Actual click logic else")
                        isLoading =false
                        onClick()
                    }
                }
            } else {
                interStillCounter++
                Logger.d("Click", "Actual click logic interStillCounter")
                isLoading =false
                onClick()
            }
        }
    }
}


@Preview
@Composable
fun MyScreen() {
    val context =LocalContext.current

    getCurrentActivity()?.let {activity ->
        AdConsentUtil.gatherConsent(activity) { aBoolean: Boolean? ->
            AdsShowingClass.loadOpenAppAds(activity)
            if (AdConsentUtil.canRequestAds(activity)) {
                Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
                    override fun run() {
//                        AdConsentUtil.showAdIfAvailableAndThen(activity) {
//                            startActivity(
//                                Intent(
//                                    activity,
//                                    LifecycleAwareAdsDemoActivity::class.java
//                                )
//                            )
//                            finish()
//                        }
                    }
                }, 8000)
            }
        }
    }
    Column {
        Button(modifier = Modifier.oneAdClickable {

        }, onClick = {}) {
            Text("Click Me")
        }
        Text(modifier = Modifier.oneAdClickable {
            Logger.makeTextToast(context, "Hello Ads Click", Toast.LENGTH_SHORT)
        }, text = "TestAd Click")
    }

}

@Composable
fun getCurrentActivity(): Activity? {
    val context = LocalContext.current
    return context as? Activity ?: (context as? ContextWrapper)?.baseContext as? Activity
}