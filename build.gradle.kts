import groovy.xml.dom.DOMCategory.attributes
import org.gradle.kotlin.dsl.provider.gradleKotlinDslJarsOf

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    buildSrcVersions
}

val currentScmVersion: String by ext

allprojects {
    repositories {
        google()
        jcenter()

    }
}

tasks {
    wrapper {
        gradleVersion = Versions.gradleLatestVersion
    }
    register("clean", Delete::class) {
        delete = setOf(rootProject.buildDir)
    }
}
