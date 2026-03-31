package com.chirag.googleads.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ContextThemeWrapper
import android.view.Window
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import com.chirag.googleads.R
import com.chirag.googleads.databinding.LayoutGenericDialogBinding

object GenericDialogHelper {

    fun showGenericDialog(
        context: Context,
        title: String,
        description: String,
        @DrawableRes icon: Int? = null,
        showDismissIcon: Boolean = true,
        @StyleRes themeOverlayResId: Int = R.style.ThemeOverlay_GoogleAds_GenericDialog,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        onPositiveClick: () -> Unit = {},
        onNegativeClick: () -> Unit = {},
        onDismiss: () -> Unit = {}
    ): Dialog {
        val themedContext = ContextThemeWrapper(context, themeOverlayResId)
        val dialog = Dialog(themedContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        
        val binding = LayoutGenericDialogBinding.inflate(LayoutInflater.from(themedContext))
        dialog.setContentView(binding.root)
        
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        // Close button
//        binding.btnCancel.visibility = if (showDismissIcon) View.VISIBLE else View.GONE
//        binding.btnCancel.setOnClickListener {
//            dialog.dismiss()
//            onDismiss()
//        }

        // Title
        binding.tvTitle.text = title

        // Icon
        if (icon != null) {
            binding.ivIcon.visibility = View.VISIBLE
            binding.ivIcon.setImageResource(icon)
        } else {
            binding.ivIcon.visibility = View.GONE
        }

        // Description
        binding.tvDescription.text = description

        // Buttons
        var hasButtons = false
        
        if (!negativeButtonText.isNullOrEmpty()) {
            binding.btnNegative.visibility = View.VISIBLE
            binding.btnNegative.text = negativeButtonText
            binding.btnNegative.setOnClickListener {
                onNegativeClick()
                dialog.dismiss()
            }
            hasButtons = true
        } else {
            binding.btnNegative.visibility = View.GONE
        }

        if (!positiveButtonText.isNullOrEmpty()) {
            binding.btnPositive.visibility = View.VISIBLE
            binding.btnPositive.text = positiveButtonText
            binding.btnPositive.setOnClickListener {
                onPositiveClick()
                dialog.dismiss()
            }
            hasButtons = true
        } else {
            binding.btnPositive.visibility = View.GONE
        }

//        binding.buttonContainer.visibility = if (hasButtons) View.VISIBLE else View.GONE

        dialog.show()
        return dialog
    }
}
