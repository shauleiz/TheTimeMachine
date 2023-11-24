plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.thetimemachine"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.thetimemachine"
        minSdk = 27
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.room:room-common:2.4.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //def room_version = "2.4.2"
    implementation ("androidx.room:room-runtime:2.4.2")
    implementation ("androidx.room:room-ktx:2.4.2")
    //kapt  ("androidx.room:room-compiler:2.4.2")

/*
    implementation ("androidx.room:room-runtime:$rootProject.roomVersion")
    annotationProcessor ("androidx.room:room-compiler:$rootProject.roomVersion")
    androidTestImplementation ("androidx.room:room-testing:$rootProject.roomVersion")

    implementation ("androidx.lifecycle:lifecycle-viewmodel:$rootProject.lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-livedata:$rootProject.lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-common-java8:$rootProject.lifecycleVersion")

    implementation ("androidx.constraintlayout:constraintlayout:$rootProject.constraintLayoutVersion")
    implementation ("com.google.android.material:material:$rootProject.materialVersion")*/
}