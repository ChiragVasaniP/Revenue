package com.chirag.googleads.localcache

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

internal object PreferenceManager {

    private const val PREF_NAME = "GoogleAdsModule."
    private lateinit var preferences: SharedPreferences

    // Initialize in Application class or MainActivity before using
    fun init(context: Context) {
        preferences =
            context.getSharedPreferences(PREF_NAME.plus(context.packageName), Context.MODE_PRIVATE)
    }

    fun putString(key: String, value: String) {
        if (this::preferences.isInitialized.not()) return
        preferences.edit { putString(key, value) }
    }

    fun getString(key: String, defaultValue: String = ""): String {
        if (this::preferences.isInitialized.not()) return defaultValue
        return preferences.getString(key, defaultValue) ?: defaultValue
    }

    fun putInt(key: String, value: Int) {
        if (this::preferences.isInitialized.not()) return
        preferences.edit { putInt(key, value) }
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        if (this::preferences.isInitialized.not()) return defaultValue
        return preferences.getInt(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        if (this::preferences.isInitialized.not()) return
        preferences.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        if (this::preferences.isInitialized.not()) return defaultValue
        return preferences.getBoolean(key, defaultValue)
    }

    fun putFloat(key: String, value: Float) {
        if (this::preferences.isInitialized.not()) return
        preferences.edit { putFloat(key, value) }
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        if (this::preferences.isInitialized.not()) return defaultValue
        return preferences.getFloat(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        if (this::preferences.isInitialized.not()) return
        preferences.edit { putLong(key, value) }
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        if (this::preferences.isInitialized.not()) return defaultValue
        return preferences.getLong(key, defaultValue)
    }

    fun remove(key: String) {
        if (this::preferences.isInitialized.not()) return
        preferences.edit { remove(key) }
    }

    fun clear() {
        if (this::preferences.isInitialized.not()) return
        preferences.edit { clear() }
    }

    fun contains(key: String): Boolean {
        if (this::preferences.isInitialized.not()) return false
        return preferences.contains(key)
    }
}
