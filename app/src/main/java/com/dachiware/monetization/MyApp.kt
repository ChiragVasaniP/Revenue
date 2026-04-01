package com.dachiware.monetization

import android.app.Application
import com.chirag.googleads.MyApplication
import com.chirag.googleads.base.ApplicationLifecycleManager
//import com.chirag.googleads.event.FirebaseInitializer

class MyApp : MyApplication() {
    override fun onCreate() {
        super.onCreate()
//        FirebaseInitializer.init(this)

        if (instance == null) {
            instance = this
        }
        // Initialize Application Lifecycle Manager
//        ApplicationLifecycleManager.getInstance().initialize(this)
    }


    companion object {
        var instance: MyApp? = null
    }
}
