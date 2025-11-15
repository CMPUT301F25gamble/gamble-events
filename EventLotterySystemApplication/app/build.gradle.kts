plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.eventlotterysystemapplication"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.eventlotterysystemapplication"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-firestore") // firestore service
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage") // storage bucket service
    implementation("com.firebaseui:firebase-ui-storage:9.1.1") // FirebaseUI for image loading
    implementation("com.github.bumptech.glide:glide:5.0.5") // Glide for image loading
    implementation(libs.firebase.installations)
    implementation(libs.firebase.messaging) // auth service

    //Mockito dependencies
    testImplementation("org.mockito:mockito-core:5.20.0")
    androidTestImplementation("org.mockito:mockito-android:5.20.0")

    //Unit testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.1")

    //Others
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Zxing Library
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    //Firebase messaging
    implementation("com.google.firebase:firebase-messaging:25.0.1")
}