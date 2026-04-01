package com.dachiware.monetization

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.chirag.googleads.base.ApplicationLifecycleManager
import com.chirag.googleads.base.ApplicationLifecycleManagerOpenAds

//import com.chirag.googleads.event.FirebaseInitializer

class MyApp : Application(), DefaultLifecycleObserver {
    override fun onCreate() {
        super<Application>.onCreate()
        ApplicationLifecycleManagerOpenAds.getInstance().initialize(this@MyApp)
//        FirebaseInitializer.init(this)

        if (instance == null) {
            instance = this
        }
        // Initialize Application Lifecycle Manager
//        ApplicationLifecycleManager.getInstance().initialize(this)

    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
    }


    companion object {
        var instance: MyApp? = null
    }
}
