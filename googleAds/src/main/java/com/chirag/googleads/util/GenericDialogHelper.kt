package com.chirag.googleads.util

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.chirag.googleads.R
import com.chirag.googleads.databinding.LayoutGenericDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object GenericDialogHelper {
    const val UNDEFINED_RES = 0

    @Suppress("LongParameterList")
    fun Context.createAlertDialog(
        @StringRes titleRes: Int = UNDEFINED_RES,
        @StringRes subtitleRes: Int = UNDEFINED_RES,
        @StringRes messageRes: Int = UNDEFINED_RES,
        title: String? = null,
        subtitle: String? = null,
        message: String? = null,
        @StringRes positiveRes: Int = R.string.session_lib_ok,
        positiveClick: ((itf: DialogInterface) -> Unit)? = null,
        @StringRes negativeRes: Int = UNDEFINED_RES,
        negativeClick: ((itf: DialogInterface) -> Unit)? = null,
        cancelable: Boolean = true,
        showClose: Boolean = false,
        onCancel: (() -> Unit)? = null,
        @DrawableRes imageRes: Int = UNDEFINED_RES,
        onDismiss: (() -> Unit)? = null,
    ): AlertDialog {
        val inflater = LayoutInflater.from(this)
        val alertDialogView: View? = inflater.inflate(R.layout.layout_generic_dialog, null)
        val dialog = MaterialAlertDialogBuilder(this).setView(alertDialogView).setCancelable(cancelable)
            .setOnCancelListener {
                it.dismiss()
                onCancel?.invoke()
            }.setOnDismissListener {
                onDismiss?.invoke()
            }.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.attributes?.windowAnimations = R.style.Session_Lib_Dialog_Animation

        val imageResource = if (imageRes == UNDEFINED_RES) {
            getResourceFromAttribute(R.attr.sessionLibIconLogo)
        } else {
            imageRes
        }
        dialog.window?.setDimAmount(0.5f)

        alertDialogView?.let { parent ->

            parent.findViewById<AppCompatImageView>(hasValidResourceId("imageViewIcon"))
                ?.let { imageViewIcon ->
                    when {
                        imageResource != UNDEFINED_RES -> {
                            imageViewIcon.isVisible = true
                            imageViewIcon.setImageResource(imageResource)
                        }

                        else -> imageViewIcon.isVisible = false
                    }
                }

            val titleResult = when {
                title != null -> title
                titleRes != UNDEFINED_RES -> getString(titleRes).fromHtml()
                else -> ""
            }
            parent.findViewById<TextView>(hasValidResourceId("txtTitle"))?.text = titleResult.toString()

            val subtitleResult = when {
                subtitle != null -> subtitle
                subtitleRes != UNDEFINED_RES -> getString(subtitleRes).fromHtml()
                else -> null
            }
            parent.findViewById<TextView>(hasValidResourceId("txtSubTitle"))
                ?.setTextOrGone(subtitleResult)

            val messageResult = when {
                message != null -> message
                messageRes != UNDEFINED_RES -> getString(messageRes)
                else -> null
            }
            parent.findViewById<TextView>(hasValidResourceId("txtMessage"))
                ?.setTextOrGone(messageResult)

            parent.findViewById<View>(hasValidResourceId("buttonClose"))?.let { buttonClose ->
                buttonClose.isVisible = showClose
                buttonClose.setSafeOnClickListener {
                    onCancel?.invoke()
                    dialog.dismiss()
                }
            }

            parent.findViewById<Button>(hasValidResourceId("buttonPositive"))?.let { buttonPositive ->
                buttonPositive.setTextResOrGone(positiveRes)
                buttonPositive.setSafeOnClickListener {
                    dialog.dismiss()
                    positiveClick?.invoke(dialog)
                }
            }
            parent.findViewById<Button>(hasValidResourceId("buttonNegative"))?.let { buttonNegative ->
                buttonNegative.setTextResOrGone(negativeRes)
                buttonNegative.setSafeOnClickListener {
                    dialog.dismiss()
                    negativeClick?.invoke(dialog)
                }
            }
        }
        return dialog
    }
}
