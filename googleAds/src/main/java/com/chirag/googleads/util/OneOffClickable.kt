package com.chirag.googleads.util

import android.app.Activity
import android.content.ContextWrapper
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.chirag.googleads.AdsShowingClass
import com.chirag.googleads.localcache.CLICK_TYPE
import com.chirag.googleads.localcache.LocalAdPrefHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



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
    var interStillCounter by remember { mutableStateOf(1L) }
    var isViewClicked by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

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
            Log.d("ClickDebug", "interStillCounter: $interStillCounter, maxClick: $maxClick, elapsedTime: $elapsedTime, isViewClicked: $isViewClicked")

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
                        Log.d("Click", "Actual click logic else")
                        onClick()
                    }
                }
            } else {
                interStillCounter++
                Log.d("Click", "Actual click logic interStillCounter")
                onClick()
            }
        }
    }
}


@Preview
@Composable
fun MyScreen() {
    val context =LocalContext.current

    Column() {
        Button(modifier = Modifier.oneAdClickable {

        }, onClick = {}) {
            Text("Click Me")
        }
        Text(modifier = Modifier.oneAdClickable {
            Toast.makeText(context, "Hello Ads Click", Toast.LENGTH_SHORT).show()
        }, text = "TestAd Click")
    }

}

@Composable
fun getCurrentActivity(): Activity? {
    val context = LocalContext.current
    return context as? Activity ?: (context as? ContextWrapper)?.baseContext as? Activity
}