plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.mywebviewapp"
    compileSdk = 35 // Match targetSdk to avoid missing platform APIs on Android 15

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    ndkVersion = "25.1.8937393"

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("../keystore/release-key.jks")
            storePassword = "RiyaDSA@2025"
            keyAlias = "riyadsa"
            keyPassword = "RiyaDSA@2025"
        }
    }

    defaultConfig {
        applicationId = "com.riyadsa.app"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        resValue("string", "app_name", "Riya DSA")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "build_time", System.currentTimeMillis().toString())
            buildConfigField("String", "BUILD_TIMESTAMP", "\"${System.currentTimeMillis()}\"")
        }
        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "build_time", System.currentTimeMillis().toString())
            buildConfigField("String", "BUILD_TIMESTAMP", "\"${System.currentTimeMillis()}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.6.1")
}