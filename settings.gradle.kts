rootProject.name = "LabOne"

include(":app")

pluginManagement {
    repositories {
        maven {
            url = uri("https://artifactory-external.vkpartner.ru/artifactory/maven")
        }
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application" -> useModule("com.android.tools.build:gradle:${requested.version}")
                "dagger.hilt.android.plugin" -> useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
                "androidx.navigation.safeargs.kotlin" -> useModule("androidx.navigation:navigation-safe-args-gradle-plugin:${requested.version}")
            }
        }
    }
}


plugins {
    id("de.fayard.refreshVersions") version "0.51.0"
////                            # available:"0.60.0"
////                            # available:"0.60.1"
////                            # available:"0.60.2"
////                            # available:"0.60.3"
////                            # available:"0.60.4"
////                            # available:"0.60.5"
}
