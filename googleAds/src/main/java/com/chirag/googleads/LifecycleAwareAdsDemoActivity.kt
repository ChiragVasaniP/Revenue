package com.chirag.googleads

import android.os.Build
import android.os.Bundle
import com.chirag.googleads.event.Logger
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.chirag.googleads.util.GenericDialogHelper
import com.chirag.googleads.util.GenericDialogHelper.createAlertDialog
import com.chirag.googleads.util.OnOneOffClickListener
import com.google.android.gms.ads.rewarded.RewardItem

/**
 * Demo Activity for LifecycleAwareAdsManager
 * 
 * This activity demonstrates how to use the lifecycle-aware ads manager
 * and shows the benefits of lifecycle-aware ad management.
 */
class LifecycleAwareAdsDemoActivity : AppCompatActivity() {
    
    private val TAG = "AdsDemoActivity"
    
    private lateinit var statusTextView: TextView
    private lateinit var eventLogTextView: TextView
    private lateinit var adsContainer: ViewGroup
    private lateinit var adsManager: LifecycleAwareAdsManager
    
    private val eventLog = StringBuilder()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle_aware_ads_demo)
        initializeViews()
        Logger.i(TAG, "🚀 LifecycleAwareAdsDemoActivity created")
        addEventLog("Activity Created")
        

        initializeAdsManager()
    }
    
    private fun initializeViews() {
        statusTextView = findViewById(R.id.statusTextView)
        eventLogTextView = findViewById(R.id.eventLogTextView)
        adsContainer = findViewById(R.id.adsContainer)
        
        // Setup demo buttons
        setupDemoButtons()
        
        updateStatus("Initializing...")
    }
    
    private fun setupDemoButtons() {
        val buttonContainer = findViewById<LinearLayout>(R.id.buttonContainer)
        
        // Banner Ad Button
        val bannerButton = Button(this).apply {
            text = "Show Banner Ad"
            setOnClickListener { showBannerAd() }
        }
        buttonContainer.addView(bannerButton)
        
        // Native Ad Button
        val nativeButton = Button(this).apply {
            text = "Show Native Ad"
            setOnClickListener { showNativeAd() }
        }
        buttonContainer.addView(nativeButton)
        
        // Interstitial Ad Button
        val interstitialButton = Button(this).apply {
            text = "Show Interstitial Ad"
            setOnClickListener { showInterstitialAd() }
        }
        buttonContainer.addView(interstitialButton)
        
        // Rewarded Ad Button
        val rewardedButton = Button(this).apply {
            text = "Show Rewarded Ad"
            setOnClickListener { showRewardedAd() }
        }
        buttonContainer.addView(rewardedButton)

        val customClickButton = Button(this).apply {
            text = "Custom Click Button"
            setOnClickListener(object : OnOneOffClickListener(this@LifecycleAwareAdsDemoActivity) {
                override fun onSingleClick(v: View) {
//                    TODO("Not yet implemented")
                }
            })
        }
        buttonContainer.addView(customClickButton)

        // Generic Dialog Button
        val dialogButton = Button(this).apply {
            text = "Open Generic Dialog"
            setOnClickListener {
//                createAlertDialog(
//                    context = this@LifecycleAwareAdsDemoActivity,
//                    title = "Demo Dialog",
//                    description = "This is a generic dialog opened from LifecycleAwareAdsDemoActivity.",
//                    positiveButtonText = "Close",
//                    onPositiveClick = {
//                        addEventLog("Dialog closed")
//                    }
//                )
            }
        }
        buttonContainer.addView(dialogButton)

        // Clear Log Button
        val clearButton = Button(this).apply {
            text = "Clear Log"
            setOnClickListener { clearEventLog() }
        }

        buttonContainer.addView(clearButton)


    }
    
    private fun initializeAdsManager() {
        adsManager = LifecycleAwareAdsManager.getInstance()
        
        if (!adsManager.isInitialized()) {
            adsManager.initialize()
            addEventLog("Ads Manager initialized")
        }
        
        updateStatus("Ads Manager Ready")
        addEventLog("Ads Manager ready for use")
    }
    
    // Demo ad methods
    
    private fun showBannerAd() {
        addEventLog("Requesting Banner Ad")
        updateStatus("Loading Banner Ad...")
        
        adsManager.showBannerAds(this, adsContainer)
        addEventLog("Banner Ad request sent")
        updateStatus("Banner Ad displayed")
    }
    
    private fun showNativeAd() {
        addEventLog("Requesting Native Ad")
        updateStatus("Loading Native Ad...")
        
        adsManager.showNativeAds(this, adsContainer)
        addEventLog("Native Ad request sent")
        updateStatus("Native Ad displayed")
    }
    
    private fun showInterstitialAd() {
        addEventLog("Requesting Interstitial Ad")
        updateStatus("Loading Interstitial Ad...")
        
        adsManager.showInterstitialAd(this) {
            addEventLog("Interstitial Ad closed")
            updateStatus("Interstitial Ad completed")
        }
        addEventLog("Interstitial Ad request sent")
    }
    
    private fun showRewardedAd() {
        addEventLog("Requesting Rewarded Ad")
        updateStatus("Loading Rewarded Ad...")
        
        adsManager.showRewardAds(
            activity = this,
            onRewardEarned = { reward ->
                addEventLog("Reward earned: ${reward.amount} ${reward.type}")
                updateStatus("Reward earned!")
            },
            onAdClosed = {
                addEventLog("Rewarded Ad closed")
                updateStatus("Rewarded Ad completed")
            }
        )
        addEventLog("Rewarded Ad request sent")
    }
    
    // Lifecycle methods
    
    override fun onStart() {
        super.onStart()
        Logger.i(TAG, "▶️ LifecycleAwareAdsDemoActivity started")
        addEventLog("Activity Started")
    }
    
    override fun onResume() {
        super.onResume()
        Logger.i(TAG, "▶️ LifecycleAwareAdsDemoActivity resumed")
        addEventLog("Activity Resumed")
        updateStatus("Active")
        
        // Check ads manager status
        checkAdsManagerStatus()
    }
    
    override fun onPause() {
        super.onPause()
        Logger.i(TAG, "⏸️ LifecycleAwareAdsDemoActivity paused")
        addEventLog("Activity Paused")
        updateStatus("Paused")
    }
    
    override fun onStop() {
        super.onStop()
        Logger.i(TAG, "⏸️ LifecycleAwareAdsDemoActivity stopped")
        addEventLog("Activity Stopped")
        updateStatus("Stopped")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Logger.i(TAG, "🗑️ LifecycleAwareAdsDemoActivity destroyed")
        addEventLog("Activity Destroyed")
        updateStatus("Destroyed")
    }
    
    // Helper methods
    
    private fun checkAdsManagerStatus() {
        val status = StringBuilder()
        status.append("Ads Manager Status:\n")
        status.append("• Initialized: ${adsManager.isInitialized()}\n")
        status.append("• App in Foreground: ${adsManager.isAppInForeground()}\n")
        status.append("• Time since last ad: ${adsManager.getTimeSinceLastAd() / 1000}s\n")
        
        addEventLog("Status Check: ${status.toString().trim()}")
    }
    
    private fun updateStatus(status: String) {
        statusTextView.text = "Status: $status"
    }
    

    private fun addEventLog(event: String) {
        val timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.LocalTime.now().toString().substring(0, 8)
        } else {
            ""
        }
        val logEntry = "$timestamp - $event\n"
        eventLog.insert(0, logEntry)
        
        // Keep only last 30 events
        if (eventLog.length > 3000) {
            eventLog.setLength(3000)
        }
        
        eventLogTextView.text = eventLog.toString()
    }
    
    private fun clearEventLog() {
        eventLog.setLength(0)
        eventLogTextView.text = ""
        addEventLog("Event log cleared")
    }
    
    // Public methods for external access
    
    fun getAdsManager(): LifecycleAwareAdsManager = adsManager
    
    fun getEventLog(): String = eventLog.toString()
}
