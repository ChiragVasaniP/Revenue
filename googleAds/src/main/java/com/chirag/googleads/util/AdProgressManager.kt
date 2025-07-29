package com.chirag.googleads.util

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chirag.googleads.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



object AdProgressManager {
    private var loadingDialog: Dialog? = null

    // ViewBinding class for our custom dialog layout
    private class DialogBinding private constructor(
        val root: View,
        val progressIndicator: CircularProgressIndicator,
        val tvLoadingMessage: TextView
    ) {
        companion object {
            fun inflate(layoutInflater: LayoutInflater): DialogBinding {
                val root = layoutInflater.inflate(R.layout.custom_ad_loading_dialog, null)
                return DialogBinding(
                    root,
                    root.findViewById(R.id.progress_indicator),
                    root.findViewById(R.id.tv_loading_message)
                )
            }
        }
    }

    fun showAdLoadingDialog(context: Context) {
        dismissDialog() // Ensure any existing dialog is dismissed

        val binding = DialogBinding.inflate(LayoutInflater.from(context))

        // Customize the view
        binding.tvLoadingMessage.text = context.getString(R.string.ad_loading)
        binding.progressIndicator.isIndeterminate = true

        loadingDialog = MaterialAlertDialogBuilder(context, R.style.RoundedDialogTheme)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        // Apply window background and dim
        loadingDialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.2f)
        }

        // Only show if the context is still valid
        if (context is AppCompatActivity && !context.isFinishing) {
            loadingDialog?.show()
        }
    }

    fun dismissDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    fun loadAdWithProgress(
        context: Context,
        adType: String = "ad",
        loadFunction: suspend () -> Boolean,
        callback: (Boolean) -> Unit
    ) {
        showAdLoadingDialog(context)

        CoroutineScope(Dispatchers.IO).launch {
            val success = loadFunction()

            withContext(Dispatchers.Main) {
                dismissDialog()
                callback(success)
            }
        }
    }
}