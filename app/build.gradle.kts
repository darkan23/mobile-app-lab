plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    kotlin("plugin.serialization")
    id("dagger.hilt.android.plugin") version "2.38.1"
    id("androidx.navigation.safeargs.kotlin") version "2.3.0"
}

android {
    compileSdk = 31


    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.labone"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("io.github.microutils:kotlin-logging:_")
    implementation("org.slf4j:slf4j-jdk14:_")

    implementation(Kotlin.stdlib.jdk8)
    implementation(KotlinX.serialization.json)
    implementation(KotlinX.coroutines.android)
    // COROUTINES: remove when migrated to coroutines
    implementation(KotlinX.coroutines.rx2)

    // COROUTINES: remove when migrated to coroutines
    implementation("io.reactivex.rxjava2:rxjava:_")
    implementation("io.reactivex.rxjava2:rxkotlin:_")
    implementation("io.reactivex.rxjava2:rxandroid:_")



    implementation(Square.retrofit2.retrofit)
    implementation(JakeWharton.retrofit2.converter.kotlinxSerialization)
    // COROUTINES: remove when migrated to coroutines
    implementation(Square.retrofit2.adapter.rxJava2)
    implementation(Square.okHttp3.okHttp)
    implementation(Square.okHttp3.loggingInterceptor)

    implementation(AndroidX.archCore.runtime)
    implementation(AndroidX.core)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.fragmentKtx)
    implementation(AndroidX.annotation)
    implementation(AndroidX.exifInterface)
    implementation(AndroidX.recyclerView)
    implementation(Google.android.material)
    implementation(AndroidX.vectorDrawable)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.lifecycle.process)
    implementation(AndroidX.lifecycle.commonJava8)
    implementation(AndroidX.lifecycle.runtimeKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.preferenceKtx)

    implementation(AndroidX.navigation.fragmentKtx)
    implementation(AndroidX.navigation.uiKtx)

    kapt(AndroidX.room.compiler)

    implementation(AndroidX.room.ktx)
    // COROUTINES: remove when migrated to coroutines
    implementation(AndroidX.room.rxJava2)

    implementation("com.airbnb.android:mavericks:_")
    // COROUTINES: remove when migrated to coroutines
    implementation("com.airbnb.android:mavericks-rxjava2:_")
    kapt("com.airbnb.android:epoxy-processor:_")
    implementation("com.airbnb.android:epoxy:_") {
        exclude(group = "com.android.support")
    }
    implementation("com.android.installreferrer:installreferrer:_")

    implementation(Splitties.alertdialogAppcompat)
    implementation(Splitties.mainhandler)
    implementation(Splitties.preferences)
    implementation(Splitties.resources)
    implementation(Splitties.systemservices)
    implementation(Splitties.toast)

    kapt(Google.Dagger.hilt.compiler)
    implementation(Google.Dagger.hilt.android)

    implementation("me.leolin:ShortcutBadger:_")

    kapt("com.github.bumptech.glide:compiler:_")
    implementation("com.github.bumptech.glide:glide:_")
    implementation("com.github.bumptech.glide:okhttp3-integration:_")

    testImplementation(Testing.junit4)
    testImplementation(Testing.mockK.android)
    testImplementation(Kotlin.test.junit)
    testImplementation(Testing.kotest.assertions.core)
    testImplementation(Testing.kotest.runner.junit4)
    testImplementation(Square.okHttp3.mockWebServer)

    kaptAndroidTest(Google.dagger.hilt.compiler)
    androidTestImplementation(Google.dagger.hilt.android.testing)
    androidTestImplementation(Testing.junit4)
    androidTestImplementation("org.assertj:assertj-core:_")
    androidTestImplementation(AndroidX.test.core)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.runner)
    androidTestImplementation(AndroidX.test.rules)
    androidTestImplementation(AndroidX.archCore.testing)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.test.espresso.contrib)
    androidTestImplementation(AndroidX.test.espresso.idlingResource)
    androidTestImplementation(Kotlin.test.junit)
    androidTestImplementation(Testing.mockK.android)
}
