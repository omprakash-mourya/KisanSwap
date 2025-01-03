plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.kisanswap.kisanswap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kisanswap.kisanswap"
        minSdk = 27
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.8")

    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material)
    val compose_version = "1.6.7"
    val coroutine_android = "1.6.4"
    val coroutine_core = "1.6.4"
    val lifecycle_version = "2.6.0-alpha01"
    val room = "2.6.0"

    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.transportation.consumer)

    //Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //lottie animation
    implementation("com.airbnb.android:lottie-compose:6.0.0")

    // YouTube player
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.0.1")

    //Exoplayer
    implementation("androidx.media3:media3-exoplayer:1.1.0") // [Required] androidx.media3 ExoPlayer dependency
    implementation("androidx.media3:media3-session:1.1.0") // [Required] MediaSession Extension dependency
    implementation("androidx.media3:media3-ui:1.1.0") // [Required] Base Player UI

    implementation("androidx.media3:media3-exoplayer-dash:1.1.0") // [Optional] If your media item is DASH
    implementation("androidx.media3:media3-exoplayer-hls:1.1.0") // [Optional] If your media item is HLS (m3u8..)
    implementation("androidx.media3:media3-exoplayer-smoothstreaming:1.1.0") // [Optional] If your media item is smoothStreaming
    //exoplayer
    implementation ("com.google.android.exoplayer:exoplayer:2.18.7")

    implementation("com.google.firebase:firebase-appcheck-ktx:16.0.0")
    implementation("com.google.firebase:firebase-appcheck-playintegrity:16.0.0")

    implementation("com.github.bumptech.glide:glide:4.11.0")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:$compose_version")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth-api-phone")
    implementation("com.google.firebase:firebase-appcheck-debug")

    implementation("com.firebaseui:firebase-ui-auth:8.0.0")

    implementation("com.google.maps.android:maps-compose:2.0.0")
    implementation("com.google.android.gms:play-services-maps:18.0.2")


    implementation("com.google.android.gms:play-services-auth:1.5.0-alpha05")

    androidTestImplementation("com.google.android.gms:play-services-auth:1.5.0-alpha05")

    //ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    implementation("io.coil-kt:coil-compose:2.1.0")

    //kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine_android")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutine_core")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // Accompanist Pager
    implementation("com.google.accompanist:accompanist-pager:0.30.1")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.30.1")

    implementation ("androidx.navigation:navigation-compose:2.5.3")

    //room database
    implementation ("androidx.room:room-runtime:$room")
    kapt ("androidx.room:room-compiler:$room")
    implementation ("androidx.room:room-ktx:$room")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}