# Google Ads Monetization Module

A lifecycle-aware, easy-to-integrate Android library for Google AdMob monetization.

## Features
- **Lifecycle-Aware**: Automatically manages ad preloading, pausing, and cleanup based on the app and activity lifecycle.
- **Support for All Ad Types**: Banner, Native, Interstitial, Rewarded, Rewarded Interstitial, and App Open ads.
- **Smart Frequency Management**: Prevents ad fatigue with configurable minimum intervals between ads.
- **Centralized Configuration**: Manage all Ad Unit IDs and states via `LocalAdPrefHelper`.
- **Consent Management**: Integrated User Messaging Platform (UMP) support for GDPR/Privacy compliance.

## Prerequisites
1.  **AdMob Account**: Have a valid Google AdMob account.
2.  **AdMob App ID**: Obtain your unique App ID from the AdMob console.

## Integration

### 1. Project-level `settings.gradle.kts`
Include the module in your project:
```kotlin
include(":googleAds")
```

### 2. App-level `build.gradle.kts`
Add the dependency:
```kotlin
dependencies {
    implementation(project(":googleAds"))
}
```

### 3. `AndroidManifest.xml`
Add your AdMob App ID:
```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="YOUR_ADMOB_APP_ID_HERE" />
```

## Initialization

### 1. In Application Class
Initialize the lifecycle manager and ads manager in your `Application.onCreate()`:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Lifecycle Manager
        ApplicationLifecycleManager.getInstance().initialize(this)
        
        // Initialize Ads Manager
        LifecycleAwareAdsManager.getInstance().initialize()
        
        // Optional: Enable/Disable Logs
        LocalAdPrefHelper.enableAdsLogging(BuildConfig.DEBUG)
    }
}
```

## Configuration (LocalAdPrefHelper)

The `LocalAdPrefHelper` provides a central way to configure ad unit IDs and module behavior.

| Method | Description |
| :--- | :--- |
| `setBannerAdId(id)` | Sets the Google Banner Ad ID |
| `setInterstitialAdId(id)` | Sets the Google Interstitial Ad ID |
| `setNativeAdId(id)` | Sets the Google Native Ad ID |
| `setRewardedAdId(id)` | Sets the Google Rewarded Ad ID |
| `setRewardedInterstitialAdId(id)` | Sets the Google Rewarded Interstitial Ad ID |
| `setAppOpenAdId(id)` | Sets the Google App Open Ad ID |
| `setAdsEnabled(boolean)` | Globally enables or disables ads |
| `setTestDeviceIdsList(list)` | Sets test device IDs for debugging |
| `enableAdsLogging(boolean)` | Enables internal module logging |

## Usage Examples

### Showing Banner Ads
```kotlin
val adsContainer = findViewById<ViewGroup>(R.id.adsContainer)
LifecycleAwareAdsManager.getInstance().showBannerAds(this, adsContainer)
```

### Showing Native Ads
```kotlin
val adsContainer = findViewById<ViewGroup>(R.id.adsContainer)
LifecycleAwareAdsManager.getInstance().showNativeAds(this, adsContainer)
```

### Showing Interstitial Ads
Automatically respects the minimum frequency interval (default 30s).
```kotlin
LifecycleAwareAdsManager.getInstance().showInterstitialAd(this) {
    // Logic to execute after ad is closed or if ad fails to show
    proceedToNextActivity()
}
```

### Showing Rewarded Ads
```kotlin
LifecycleAwareAdsManager.getInstance().showRewardAds(
    activity = this,
    onRewardEarned = { reward ->
        println("User earned: ${reward.amount} ${reward.type}")
    },
    onAdClosed = {
        // Continue flow
    }
)
```

### Handling Consent (GDPR)
Call this in your first activity to gather user consent:
```kotlin
AdConsentUtil.gatherConsent(this) { canRequestAds ->
    if (canRequestAds) {
        // Initialize or load ads
    }
}
```

## Debugging
- Use `LocalAdPrefHelper.enableAdsLogging(true)` to see detailed logs in Logcat under the tag `LifecycleAwareAds`.
- The module includes a demo activity: `LifecycleAwareAdsDemoActivity` for testing all features.
