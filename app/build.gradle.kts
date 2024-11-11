plugins {
    id("com.android.application") version "8.2.0-rc02"
    kotlin("android")
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.8.10"
    kotlin("plugin.serialization")
    id("dagger.hilt.android.plugin") version "2.50"
    id("androidx.navigation.safeargs.kotlin") version "2.7.7"
}

android {
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    ndkVersion = "23.1.7779620"

    kotlinOptions {
        languageVersion = "1.7"
        jvmTarget = JavaVersion.VERSION_17.toString()
        javaParameters = true
        freeCompilerArgs += "-Xjsr305=strict"
        freeCompilerArgs += "-Xjvm-default=all"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false

            val extra = (this as ExtensionAware).extra
            extra["enableCrashlytics"] = false
            extra["alwaysUpdateBuildId"] = false
        }
    }

    defaultConfig {
        applicationId = "ru.labone"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        proguardFile("proguard-app.pro")
        proguardFile("proguard-retrofit.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    packaging {
        resources {
            excludes.add("META-INF/AL2.0")
            excludes.add("META-INF/LGPL2.1")
        }
    }
    namespace = "com.example.labone"
}

kapt {
    useBuildCache = true
    correctErrorTypes = true
    includeCompileClasspath = false
    javacOptions {
        option("-Xmaxerrs", 10_000)
        option("-Xmaxwarns", 10_000)
    }
}

repositories {
    maven {
        url = uri("https://artifactory-external.vkpartner.ru/artifactory/maven")
    }
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation("io.github.microutils:kotlin-logging:_")
    implementation("org.slf4j:slf4j-jdk14:_")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation(KotlinX.serialization.json)
    implementation(KotlinX.coroutines.android)
    // COROUTINES: remove when migrated to coroutines
    implementation(KotlinX.coroutines.rx2)

    // COROUTINES: remove when migrated to coroutines
    implementation("io.reactivex.rxjava2:rxjava:_")
    implementation("io.reactivex.rxjava2:rxkotlin:_")
    implementation("io.reactivex.rxjava2:rxandroid:_")
    implementation("com.google.code.gson:gson:_")

    implementation("ru.rustore.sdk:pushclient:6.3.0")

    implementation("com.jenzz.appstate:appstate:_")
    implementation("com.jenzz.appstate:adapter-rxjava2:_")

    implementation(Square.retrofit2.retrofit)
    implementation(JakeWharton.retrofit2.converter.kotlinxSerialization)
    // COROUTINES: remove when migrated to coroutines
    implementation(Square.retrofit2.adapter.rxJava2)
    implementation(Square.okHttp3.okHttp)
    implementation(Square.okHttp3.loggingInterceptor)

    implementation(AndroidX.archCore.runtime)
    implementation(AndroidX.core)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.fragment.ktx)
    implementation(AndroidX.annotation)
    implementation(AndroidX.exifInterface)
    implementation(AndroidX.recyclerView)
    implementation(Google.android.material)
    implementation(AndroidX.vectorDrawable)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.lifecycle.process)
    implementation(AndroidX.lifecycle.commonJava8)
    implementation(AndroidX.lifecycle.runtime.ktx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.preference.ktx)

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
    implementation("androidx.viewpager2:viewpager2:_")
    implementation("me.relex:circleindicator:_")
    implementation("io.coil-kt:coil:_")
    implementation("com.google.guava:guava:_")
    implementation("org.apache.tika:tika-core:_")

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

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            languageVersion = "1.7"
            jvmTarget = JavaVersion.VERSION_17.toString()
            javaParameters = true
            freeCompilerArgs += "-Xjsr305=strict"
            freeCompilerArgs += "-Xjvm-default=all"
        }
    }
}