plugins {
    `kotlin-dsl`
}

group = "io.github.romanfabio.voidapps.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.spotless.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.kover.gradlePlugin)
    compileOnly(libs.aboutlibraries.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "void.android.application"
            implementationClass = "VoidAndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = "void.android.library"
            implementationClass = "VoidAndroidLibraryPlugin"
        }
        register("compose") {
            id = "void.compose"
            implementationClass = "VoidComposePlugin"
        }
        register("hilt") {
            id = "void.hilt"
            implementationClass = "VoidHiltPlugin"
        }
        register("quality") {
            id = "void.quality"
            implementationClass = "VoidQualityPlugin"
        }
        register("licenses") {
            id = "void.licenses"
            implementationClass = "VoidLicensesPlugin"
        }
    }
}
