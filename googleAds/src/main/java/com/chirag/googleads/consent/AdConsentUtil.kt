package com.chirag.googleads.consent


import android.app.Activity
import android.content.Context
import com.chirag.googleads.event.Logger
import android.widget.Toast
import com.chirag.googleads.BuildConfig
import com.chirag.googleads.MyApplication
import com.chirag.googleads.adsUtil.OnShowAdCompleteListener
import com.chirag.googleads.localcache.LocalAdPrefHelper
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Utility object to handle AdMob initialization, consent gathering,
 * and App Open Ad loading/showing in a centralized and reusable way.
 */
 object AdConsentUtil {

    // Ensures the Mobile Ads SDK is only initialized once.
    private val isInitialized = AtomicBoolean(false)

    // Tracks whether the user consent process has finished in this session.
    private val isConsentFetched = AtomicBoolean(false)

    /**
     * Initializes Google Mobile Ads SDK if not already initialized.
     * Should only be called if user has given consent.
     *
     * @param context Application or Activity context.
     */
    private fun initAdSdkIfNeeded(context: Context) {
        if (isInitialized.getAndSet(true)) return // Prevent re-initialization
        // Set test device IDs for debugging ads (optional)
       val deviceTestId = listOf(MyApplication.TEST_DEVICE_HASHED_ID).takeIf { BuildConfig.DEBUG }?: LocalAdPrefHelper.getTestDeviceIds()

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(deviceTestId)
                .build()
        )

        MobileAds.initialize(context) {}

        Logger.d("AdUtil", "Ad SDK initialized.")
    }

    fun loadOpenAppAds(context: Activity){
        // Initialize Mobile Ads SDK on a background thread
        if (canRequestAds(context)){
            CoroutineScope(Dispatchers.Main).launch {
                // Load the App Open Ad on the main thread
                (context.applicationContext as? MyApplication)?.loadAd(context)
            }
        }

    }

    /**
     * Gathers user consent using Google’s Consent SDK.
     * Once consent is complete, calls the callback to continue flow.
     *
     * @param activity Current activity
     * @param onConsentComplete Callback with whether ads can be requested
     */
    fun gatherConsent(
        activity: Activity,
        onConsentComplete: (canRequestAds: Boolean) -> Unit
    ) {
        val consentManager = GoogleMobileAdsConsentManager.getInstance(activity.applicationContext)

        // Start the consent form flow (if required)
        consentManager.gatherConsent(activity) { consentError ->
            if (consentError != null) {
                Logger.w("AdUtil", "${consentError.errorCode}: ${consentError.message}")
            }

            isConsentFetched.set(true)

            val canRequest = consentManager.canRequestAds
            if (canRequest) {
                initAdSdkIfNeeded(activity.applicationContext)
            }

            onConsentComplete(canRequest)
        }
    }

    /** First check userNeed to Show Privacy Policy in app siode
     * googleMobileAdsConsentManager.isPrivacyOptionsRequired using below method
     * */
    fun isNeedToShowGooglePrivacyPolicyOption(activity: Activity) {
        val consentManager = GoogleMobileAdsConsentManager.getInstance(activity.applicationContext)

        consentManager.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Logger.makeTextToast(activity, formError.message, Toast.LENGTH_SHORT)
            }
        }
    }


    /**
     * Shows App Open Ad if it's available and only proceeds after ad is completed.
     *
     * @param activity Current activity
     * @param onComplete Callback to continue app flow after ad
     */
    fun showAdIfAvailableAndThen(activity: Activity, onComplete: () -> Unit) {
        (activity.application as? MyApplication)?.showAdIfAvailable(
            activity,
            object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {
                    // Proceed only if consent is done (to avoid double navigation)
                    if (isConsentFetched.get()) {
                        onComplete()
                    }
                }
            }
        )?:onComplete.invoke()
    }

    /**
     * Utility method to check whether ads can be requested based on stored consent.
     *
     * @param context Context
     * @return true if ads can be requested
     */
    fun canRequestAds(context: Context): Boolean {
        return GoogleMobileAdsConsentManager.getInstance(context).canRequestAds
    }


    fun openGoogleAdsInspect(context: Context) {
        MobileAds.openAdInspector(context) { error ->
            // Error will be non-null if ad inspector closed due to an error.
            error?.let { Logger.makeTextToast(context, it.message, Toast.LENGTH_SHORT) }
        }
    }
}



