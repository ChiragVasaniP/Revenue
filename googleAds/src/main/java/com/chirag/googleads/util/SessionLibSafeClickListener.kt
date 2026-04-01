package com.chirag.googleads.util

import android.os.SystemClock
import android.view.View

internal class SessionLibSafeClickListener(
    private var defaultInterval: Int = 400,
    private val onDoubleClick: ((View) -> Unit)? = null,
    private val onSafeCLick: (View) -> Unit,
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        if (onDoubleClick != null && SystemClock.elapsedRealtime() - doubleClickLastDuration < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick.invoke(v)
            return
        }
        doubleClickLastDuration = SystemClock.elapsedRealtime()

        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300
        private var doubleClickLastDuration = 0L
    }
}