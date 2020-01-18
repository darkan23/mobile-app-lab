import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("com.android.application") version Versions.com_android_tools_build_gradle
    kotlin("android") version Versions.org_jetbrains_kotlin_android_gradle_plugin
    kotlin("android.extensions") version Versions.org_jetbrains_kotlin_android_gradle_plugin
    id("androidx.navigation.safeargs.kotlin") version Versions.androidx_navigation
    kotlin("kapt") version Versions.org_jetbrains_kotlin_kapt_gradle_plugin
    id("kotlinx-serialization") version Versions.org_jetbrains_kotlin_android_gradle_plugin
    id("io.fabric") version Versions.io_fabric_tools_gradle
    id("io.gitlab.arturbosch.detekt") version Versions.io_gitlab_arturbosch_detekt
}

val ignoreStaticAnalysisFailures: Boolean
    get() = System.getenv("IGNORE_STATIC_ANALYSIS_FAILURES")?.toBoolean() ?: false
val isCiBuild: Boolean
    get() = System.getenv("BUILD_NUMBER")?.toIntOrNull()?.let { it >= 0 } ?: false

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.2")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        languageVersion = "1.3"
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs += "-progressive"
        freeCompilerArgs += "-XXLanguage:+NewInference"
        freeCompilerArgs += "-XXLanguage:+InlineClasses"
        freeCompilerArgs += "-XXLanguage:+SamConversionForKotlinFunctions"
        freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
        freeCompilerArgs += "-Xuse-experimental=kotlin.ExperimentalStdlibApi"
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")

    defaultConfig {
        applicationId = "com.astroid.yodha"
        versionCode = 1
        versionName = project.version.toString()
        minSdkVersion(21)
        targetSdkVersion(29)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "COUNTERS_EMAIL", "\"darkan23@gmail.com\"")
        buildConfigField(
            "String",
            "DEFAULT_PER_QUESTION_PRODUCT",
            "\"com.pocketastrologer.pocketastrologertest.f5\""
        )

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas".toString())
            }
        }
    }

    signingConfigs {
        register("release") {
            keyAlias = "couners"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            multiDexEnabled = false

            signingConfig = signingConfigs.getByName("release")
            proguardFile("proguard-app.pro")
            proguardFile("proguard-kotlin-reflect.pro")
            proguardFile(getDefaultProguardFile("proguard-android.txt"))
        }
        getByName("debug") {
            isMinifyEnabled = false
            multiDexEnabled = true

            val extra = (this as ExtensionAware).extra
            extra["enableCrashlytics"] = false
            extra["alwaysUpdateBuildId"] = false
        }
    }

    flavorDimensions("server", "apptype")

    productFlavors {
        create("dev") {
            dimension = "server"
            versionCode = 1 + 1
            buildConfigField("String", "ENVIRONMENT", "\"DEV\"")
        }
        create("light") {
            dimension = "apptype"
            buildConfigField(
                "String",
                "COUNTERS_MARKET_URL",
                "\"market://details?id=com.astroid.yodha\""
            )
            buildConfigField(
                "String",
                "COUNTERS_MARKET_WEB_URL",
                "\"http://play.google.com/store/apps/details?id=com.astroid.yodha\""
            )
            buildConfigField(
                "String",
                "COUNTERS_SHARE_URL",
                "\"http://get.yodhaapp.com\""
            )
        }
    }

    applicationVariants.all {
        val apiUrl = when {
            name.contains("devLight", true) -> "http://localhost:8089/"
            else -> error("Variant not configured $name")
        }
        buildConfigField("String", "API_BASE_URL", "\"$apiUrl\"")
    }

    lintOptions {
        //to check unused resources with generated sources, e.g. DataBinding
        isCheckGeneratedSources = true
        isAbortOnError = !ignoreStaticAnalysisFailures

        lintConfig = file("${project.rootDir}/code_quality_tools/lint.xml")
    }

    testOptions {
        animationsDisabled = true
        unitTests.apply {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
            //see https://issuetracker.google.com/issues/78547461
            all(KotlinClosure1<Any, Test>({
                (this as Test).apply {
                    ignoreFailures = isCiBuild
                    maxParallelForks = 3
                    testLogging {
                        exceptionFormat = TestExceptionFormat.FULL
                        events = setOf(
                            TestLogEvent.PASSED,
                            TestLogEvent.SKIPPED,
                            TestLogEvent.FAILED,
                            TestLogEvent.STANDARD_OUT,
                            TestLogEvent.STANDARD_ERROR
                        )
                    }
                }
            }, this))
        }
    }
    testVariants.all {
        connectedInstrumentTestProvider?.configure {
            (this as? VerificationTask)?.ignoreFailures = isCiBuild
        }
    }
    configurations.all {
        // may there is better way to exclude Android specific deps?
        if (name.contains("testRuntime", true)) {
            exclude(module = "slf4j-android")
        }
    }
}

repositories {
    google()
    jcenter()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(Libs.kotlin_logging)
    runtimeOnly(Libs.slf4j_android)

    implementation(Libs.kotlin_stdlib_jdk8)
    implementation(Libs.kotlinx_serialization_runtime)

    implementation(Libs.rxjava)
    implementation(Libs.rxkotlin)
    implementation(Libs.rxandroid)

    implementation(Libs.crashlytics)

    implementation(Libs.retrofit)
    implementation(Libs.retrofit2_kotlinx_serialization_converter)
    implementation(Libs.adapter_rxjava2)
    implementation(Libs.okhttp)
    implementation(Libs.logging_interceptor)

    //forced core-runtime version to resolve version conflict with testing libs
    implementation(Libs.core_runtime)
    implementation(Libs.core_ktx)
    implementation(Libs.appcompat)
    implementation(Libs.fragment_ktx)
    implementation(Libs.annotation)
    implementation(Libs.exifinterface)
    implementation(Libs.cardview)
    implementation(Libs.recyclerview)
    implementation(Libs.material)
    testImplementation("io.mockk:mockk:1.9.3")
    implementation("org.json:json:20190722") {
        exclude (group = "org.json", module = "json")
    }
    implementation(Libs.vectordrawable)
    implementation(Libs.constraintlayout)
    implementation(Libs.lifecycle_process)
    implementation(Libs.lifecycle_common_java8)
    implementation(Libs.lifecycle_extensions)
    implementation(Libs.lifecycle_viewmodel_ktx)
    implementation(Libs.preference_ktx)

    implementation(Libs.navigation_fragment_ktx)
    implementation(Libs.navigation_ui_ktx)

    implementation(Libs.room_ktx)
    kapt(Libs.room_compiler)
    implementation(Libs.room_rxjava2)

    implementation(Libs.threetenabp)

    implementation(Libs.mvrx)
    kapt(Libs.epoxy_processor)
    implementation(Libs.epoxy) {
        exclude(group = "com.android.support")
    }

    implementation(Libs.splitties_alertdialog_appcompat)
    implementation(Libs.splitties_preferences)
    implementation(Libs.splitties_resources)
    implementation(Libs.splitties_systemservices)
    implementation(Libs.splitties_toast)

    kapt(Libs.dagger_compiler)
    kapt(Libs.dagger_android_processor)
    kapt(Libs.assisted_inject_processor_dagger2)
    compileOnly(Libs.assisted_inject_annotations_dagger2)
    implementation(Libs.dagger_android)

    implementation(Libs.shortcutbadger)

    kapt(Libs.com_github_bumptech_glide_compiler)
    implementation(Libs.glide)
    implementation(Libs.okhttp3_integration)

    testImplementation(Libs.junit_junit)
    testImplementation(Libs.assertj_core)
    testImplementation(Libs.equalsverifier)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.kotlin_test_junit)
    testImplementation(Libs.kotlintest_runner_junit4)
    testImplementation(Libs.mockwebserver)

    testRuntimeOnly(Libs.slf4j_jdk14)

    kaptAndroidTest(Libs.dagger_compiler)
    kaptAndroidTest(Libs.dagger_android_processor)
    androidTestImplementation(Libs.dagger_android)
    androidTestImplementation(Libs.junit_junit)
    androidTestImplementation(Libs.assertj_core)
    androidTestImplementation(Libs.androidx_test_core)
    androidTestImplementation(Libs.androidx_test_ext_junit)
    androidTestImplementation(Libs.androidx_test_runner)
    androidTestImplementation(Libs.androidx_test_rules)
    androidTestImplementation(Libs.core_testing)
    androidTestImplementation(Libs.espresso_core)
    androidTestImplementation(Libs.espresso_contrib)
    androidTestImplementation(Libs.espresso_idling_resource)
    androidTestImplementation(Libs.kotlin_test_junit)
    androidTestImplementation(Libs.mockito_core)
    androidTestImplementation(Libs.mockito_android)
}

kapt {
    generateStubs = true
}

detekt {
    ignoreFailures = ignoreStaticAnalysisFailures
    config = files("${project.rootDir}/code_quality_tools/detekt-config.yml")
}
