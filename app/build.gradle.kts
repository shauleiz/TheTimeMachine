plugins {
    id("com.android.application")
    kotlin("kapt")
    id ("androidx.room") version "2.6.1"
    id("org.jetbrains.kotlin.android")
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.product.thetimemachine"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.product.thetimemachine"
        minSdk = 27
        targetSdk = 34
        versionCode = 20004
        versionName = "2.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        create("REL01") {
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "18"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }


    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.room:room-common:2.6.1")
    implementation ("androidx.activity:activity:1.9.0")
    implementation ("androidx.fragment:fragment:1.7.1")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.2")
    implementation("androidx.compose.ui:ui-android:1.7.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.compose.material3:material3-android:1.3.1")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.7.0")
    implementation("androidx.compose.ui:ui-tooling-preview-desktop:1.7.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.1")
    implementation("androidx.databinding:databinding-runtime:8.6.1")
    implementation("androidx.datastore:datastore-android:1.1.1")
    implementation("androidx.navigation:navigation-compose:2.8.5")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //def room_version = '2.4.2'
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    androidTestImplementation ("androidx.room:room-testing:2.6.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.0")

    //implementation ("android.arch.persistence.room:compiler:2.6.0")
    kapt  ("androidx.room:room-compiler:2.6.1")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.room:room-gradle-plugin:2.6.1")
    //implementation ("com.github.wisnukurniawan:date-time-range-picker-android:1.0.10")
    implementation ("com.squareup:android-times-square:1.7.11")
    implementation ("com.google.code.gson:gson:2.8.5")
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation ("androidx.constraintlayout:constraintlayout-core:1.0.4")
    implementation ("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.material3:material3:1.3.1")

    // The compose calendar library for Android
    implementation("com.kizitonwose.calendar:compose:2.6.0")

    // The Compose Datastore Preferences
    implementation ("androidx.datastore:datastore-preferences:1.1.2")
}