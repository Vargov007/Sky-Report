import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.example.skyreport"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.skyreport"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject WEB_CLIENT_ID into BuildConfig safely
        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"${localProperties.getProperty("WEB_CLIENT_ID") ?: ""}\""
        )
        buildConfigField(
            "String",
            "API_KEY",
            "\"${localProperties.getProperty("API_KEY")?: ""}\""
        )
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.googleid)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Retrofit for network calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coroutines for asynchronous operations
    implementation(libs.kotlinx.coroutines.android)
    implementation (libs.kotlinx.coroutines.play.services)


// Lifecycle components for ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.play.services.location)

}