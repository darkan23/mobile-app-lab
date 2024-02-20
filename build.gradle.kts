buildscript {
}

internal val currentVersions = project.version

allprojects {
    group = "com.example.labone"
    version = currentVersions
}

tasks {
    register("clean", Delete::class) {
        delete = setOf(rootProject.layout.buildDirectory)
    }
}
