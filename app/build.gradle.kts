plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android") version "2.51"
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"

}

android {
    namespace = "com.example.taskmanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.taskmanager"
        minSdk = 24
        targetSdk = 35
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
//
//dependencies {
//    val room_version = "2.7.1"
//    val hiltVersion = "2.51"
//
//    // Room with KSP
//    implementation("androidx.room:room-runtime:$room_version")
//    ksp("androidx.room:room-compiler:$room_version")
//
//    // Hilt with KAPT
//    implementation("com.google.dagger:hilt-android:$hiltVersion")
//    kapt("com.google.dagger:hilt-compiler:$hiltVersion")
//
//    // Hilt Navigation Compose
//    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
//
//    // AndroidX Lifecycle (Hilt integrates with this for ViewModel)
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
//    kapt("androidx.lifecycle:lifecycle-compiler:2.7.0")
//
//    // Retrofit & Moshi
//    implementation("com.google.code.gson:gson:2.10.1")
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation(libs.retrofit)
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.1")
//
//    // Compose & AndroidX
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material3)
//
//    // Testing
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
//}

dependencies {
    val room_version = "2.7.1"
    val hiltVersion = "2.51"
    val lifecycle_version = "2.7.0"
    val retrofit_version = "2.9.0"
    val gson_version = "2.10.1"
    val kotlin_serialization_version = "1.6.3"
    val kotlin_metadata_jvm_version = "0.5.1"
    val okhttpVersion = "4.11.0"


    //datastore

    implementation("androidx.datastore:datastore-preferences:1.0.0")



    // Room with KSP
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // Hilt with KSP (Migrating - comment out KAPT lines for now)
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    // kapt("com.google.dagger:hilt-compiler:$hiltVersion") // Comment out KAPT
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.14.0") // or latest


    // Hilt Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // AndroidX Lifecycle with KSP (Migrating - comment out KAPT line)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    ksp("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
    // kapt("androidx.lifecycle:lifecycle-compiler:$lifecycle_version") // Comment out KAPT

    // Retrofit & Gson (Consolidated)
    implementation("com.google.code.gson:gson:$gson_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")

    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlin_serialization_version")
//    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:$kotlin_metadata_jvm_version")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    // Compose & AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)



    // Testing
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
}