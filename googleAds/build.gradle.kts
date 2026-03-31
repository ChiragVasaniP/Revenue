plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    `maven-publish`
}

group = "com.chirag.googleads"
version = "v1.0.0-alpha13"

android {
    namespace = "com.chirag.googleads"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        multiDexEnabled = true
        buildFeatures.buildConfig = true
    }

    buildFeatures {
        compose = true

        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15" // Use the version compatible with your Kotlin plugin
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    // Material Design 3
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.runtime:runtime:1.6.7") // Example version, use the latest stable


    implementation ("androidx.lifecycle:lifecycle-process:2.9.2")
    implementation ("androidx.multidex:multidex:2.0.1")

    implementation ("com.google.android.gms:play-services-ads:24.8.0")
    implementation ("com.google.android.ump:user-messaging-platform:4.0.0")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ChiragVasaniP/chirag-monetization")
            credentials {
                username = "ChiragVasaniP"
                password = "jp_pqooocpkgf3lb6p9chgeakao0g"
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
