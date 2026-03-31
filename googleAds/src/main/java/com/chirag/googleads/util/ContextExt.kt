package com.chirag.googleads.util

import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity

fun Context.getResourceFromAttribute(attributeId: Int): Int {
    val typedValue = TypedValue()
    val success = theme.resolveAttribute(attributeId, typedValue, true)
    if (success) {
        val imageResId = typedValue.resourceId
        return imageResId
    }
    return 0
}

fun FragmentActivity.isValidActivityState(): Boolean {
    return isFinishing.not() && isDestroyed.not() && supportFragmentManager.isStateSaved.not()
}

fun Context.hasValidResourceId(id: String): Int {
    val resourceId = resources?.getIdentifier(id, "id", packageName)
    return if (resourceId != null && resourceId != 0) {
        resourceId
    } else {
        -1
    }
}

fun CharSequence?.isNotNullOrEmpty() = !isNullOrEmpty()
fun CharSequence?.isNotNullOrBlank() = !isNullOrBlank()

fun TextView.setTextOrGone(text: CharSequence?) {
    isVisible = text.isNotNullOrEmpty()
    this.text = text
}

fun TextView.setTextResOrGone(@StringRes res: Int) {
    isVisible = res != 0
    if (res != 0) {
        setText(res)
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SessionLibSafeClickListener {
        it.hapticFeedbackEnabled()
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun View.setSafeOnClickListener(
    onSafeClick: (View) -> Unit,
    onDoubleClick: ((View) -> Unit)? = null,
) {
    val safeClickListener = SessionLibSafeClickListener(onSafeCLick = {
        it.hapticFeedbackEnabled()
        onSafeClick(it)
    }, onDoubleClick = {
        it.hapticFeedbackEnabled()
        onDoubleClick?.invoke(it)
    })
    setOnClickListener(safeClickListener)
}

inline fun View.hapticFeedbackEnabled() {
    this.isHapticFeedbackEnabled = true
    if (SDK_INT >= Build.VERSION_CODES.P) {
        this.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
    } else {
        this.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }
}


inline fun String?.fromHtml(): Spanned {
    return if (this == null) SpannableString("")
    else if (SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(this.trim(), Html.FROM_HTML_MODE_LEGACY);
    } else {
        return Html.fromHtml(this.trim())
    }
}
