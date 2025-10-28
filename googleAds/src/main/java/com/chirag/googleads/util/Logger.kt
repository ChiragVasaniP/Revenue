package com.chirag.googleads.util

import com.chirag.googleads.util.Logger

/** 
 * Internal Logger — used only within the library for debug messages.
 * Controlled by [isLoggingEnabled] flag.
 */
internal object Logger {

    /** Flag to enable or disable logs globally within the library */
    internal var isLoggingEnabled: Boolean = false

    /** Default log tag */
    private const val DEFAULT_TAG = "DachiwareLib"

    /** Debug log */
    internal fun d(tag: String = DEFAULT_TAG, message: String) {
        if (isLoggingEnabled) Logger.d(tag, message)
    }

    /** Info log */
    internal fun i(tag: String = DEFAULT_TAG, message: String) {
        if (isLoggingEnabled) Logger.i(tag, message)
    }

    /** Warning log */
    internal fun w(tag: String = DEFAULT_TAG, message: String) {
        if (isLoggingEnabled) Logger.w(tag, message)
    }

    /** Error log */
    internal fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) {
            if (throwable != null) Logger.e(tag, message, throwable)
            else Logger.e(tag, message)
        }
    }

    /** Verbose log */
    internal fun v(tag: String = DEFAULT_TAG, message: String) {
        if (isLoggingEnabled) Logger.v(tag, message)
    }
}
