package com.chirag.googleads.event

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.chirag.googleads.BuildConfig
import com.chirag.googleads.event.Logger.isLoggingEnabled


/**
 * Internal Logger — used only within the library for debug messages.
 * Controlled by [isLoggingEnabled] flag.
 */
internal object Logger {

    /** Flag to enable or disable logs globally within the library */
    internal var isLoggingEnabled: Boolean = false


    /**
     * TODO Use this flag if we need to add print enable
     * */
    var isNeedToAddPrefixDefault: Boolean = false

    /** Default log tag */
    private const val DEFAULT_TAG = "DachiwareLib"

    private fun getPreFixTags(tag: String) = tag


    /** Debug log */
    internal fun d(tag: String = DEFAULT_TAG, message: String) {
        if (isLoggingEnabled) Log.d(getPreFixTags(tag), message)
    }

    /** Info log */
    internal fun i(tag: String = DEFAULT_TAG, message: String) {
        if (isLoggingEnabled) Log.i(getPreFixTags(tag), message)
    }

    /** Warning log */
    internal fun w(tag: String = DEFAULT_TAG, message: String) {
        if (isLoggingEnabled) Log.w(getPreFixTags(tag), message)
    }

    /** Error log */
    internal fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            if (throwable != null) Log.e(getPreFixTags(tag), message, throwable)
            else Log.e(getPreFixTags(tag), message)
        }
    }

    /** Verbose log */
    internal fun v(tag: String = DEFAULT_TAG, message: String) {
        if (isLoggingEnabled) Log.v(getPreFixTags(tag), message)
    }

    internal fun makeTextToast(context: Context, message: CharSequence, duration: Int) {
//        FirebaseInitializer.init(context)
//        FirebaseInitializer.logEvent("Logger",mapOf("Logger" to message.toString()))
        if (BuildConfig.DEBUG) Toast.makeText(context, message, duration).show()
    }
}