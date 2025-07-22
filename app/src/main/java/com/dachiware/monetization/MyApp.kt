package com.dachiware.monetization

import android.app.Application
import com.chirag.googleads.MyApplication
import com.chirag.googleads.base.ApplicationLifecycleManager

class MyApp : MyApplication() {
    override fun onCreate() {
        super.onCreate()
        if (instance == null) {
            instance = this
        }
        // Initialize Application Lifecycle Manager
        ApplicationLifecycleManager.getInstance().initialize(this)
    }


    companion object {
        var instance: MyApp? = null
    }
}
