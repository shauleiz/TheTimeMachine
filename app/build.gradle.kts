plugins {
    id("com.android.application")
    kotlin("kapt") version "1.9.23"
    id ("androidx.room") version "2.6.1"
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
        versionCode = 1005
        versionName = "1.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }

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

}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.room:room-common:2.6.1")
    implementation ("androidx.activity:activity:1.8.2")
    implementation ("androidx.fragment:fragment:1.6.2")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //def room_version = '2.4.2'
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    androidTestImplementation ("androidx.room:room-testing:2.6.1")
    implementation("androidx.preference:preference-ktx:1.2.1")

    //implementation ("android.arch.persistence.room:compiler:2.6.0")
    //kapt  ("androidx.room:room-compiler:2.4.2")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.room:room-gradle-plugin:2.6.1")
}