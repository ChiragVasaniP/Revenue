package com.chirag.googleads.util

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.chirag.googleads.R

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun LoadingProgressDialog(
    isVisible: Boolean,
    @StringRes loadingText: Int = R.string.ad_loading
) {
    val context = LocalContext.current
    val isDarkTheme = when (context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        else -> false
    }

    val backgroundColor = if (isDarkTheme) Color(0xFF424242) else Color.White
    val contentColor = if (isDarkTheme) Color.White else Color.Black
    val progressColor = if (isDarkTheme) Color(0xFFBB86FC) else Color(0xFF6200EE)
    AnimatedVisibility(isVisible) {
        Dialog(
            onDismissRequest = { /* Prevent dismiss when touching outside */ },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = progressColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(loadingText),
                        color = contentColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }


}

@ThemePreviews()
@Composable
fun LoadingProgressDialogPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingProgressDialog(
            isVisible = true,
        )
    }
}


@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
annotation class ThemePreviews