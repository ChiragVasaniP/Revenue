package com.chirag.googleads.util

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun GenericDialog(
    isVisible: Boolean,
    title: String,
    description: String,
    @DrawableRes icon: Int? = null,
    showDismissIcon: Boolean = true,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            val context = LocalContext.current
            val isDarkTheme = when (context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }

            val backgroundColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
            val contentColor = if (isDarkTheme) Color.White else Color.Black

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                // Top Left Cancel Option
                if (showDismissIcon) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = contentColor
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title Text
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Icon below title
                    if (icon != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Description below icon
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = description,
                        fontSize = 16.sp,
                        color = contentColor.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Positive and Negative Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (!negativeButtonText.isNullOrEmpty()) {
                            OutlinedButton(
                                onClick = onNegativeClick,
                                modifier = Modifier.weight(1f).padding(end = if (!positiveButtonText.isNullOrEmpty()) 8.dp else 0.dp)
                            ) {
                                Text(text = negativeButtonText)
                            }
                        }

                        if (!positiveButtonText.isNullOrEmpty()) {
                            Button(
                                onClick = onPositiveClick,
                                modifier = Modifier.weight(1f).padding(start = if (!negativeButtonText.isNullOrEmpty()) 8.dp else 0.dp)
                            ) {
                                Text(text = positiveButtonText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun GenericDialogPreview() {
    GenericDialog(
        isVisible = true,
        title = "Generic Title",
        description = "This is a generic description for the dialog to show how it looks with some text.",
        positiveButtonText = "Allow",
        negativeButtonText = "Deny",
        onDismiss = {}
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun GenericDialogDarkPreview() {
    GenericDialog(
        isVisible = true,
        title = "Generic Title",
        description = "This is a generic description for the dialog to show how it looks with some text.",
        positiveButtonText = "Allow",
        negativeButtonText = "Deny",
        onDismiss = {},
        showDismissIcon = false
    )
}
