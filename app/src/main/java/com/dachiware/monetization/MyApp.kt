package com.dachiware.monetization

import android.app.Application
import com.chirag.googleads.base.ApplicationLifecycleManager
import com.chirag.googleads.base.ApplicationLifecycleManagerOpenAds

//import com.chirag.googleads.event.FirebaseInitializer

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationLifecycleManagerOpenAds.getInstance().initialize(this@MyApp)
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
