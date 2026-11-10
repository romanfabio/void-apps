plugins {
    alias(libs.plugins.void.android.library)
    alias(libs.plugins.void.compose)
    alias(libs.plugins.void.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "io.github.romanfabio.voidapps.core.licenses"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.ktx)

    testImplementation(projects.core.testing)
}

// The MIT license shown in every app is the repository LICENSE file, copied at build time
// so the two can never diverge.
abstract class CopyLicenseTask : DefaultTask() {
    @get:InputFile
    abstract val licenseFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun copy() {
        licenseFile.get().asFile.copyTo(outputDir.get().file("void_license.txt").asFile, overwrite = true)
    }
}

val copyLicense = tasks.register<CopyLicenseTask>("copyVoidLicense") {
    licenseFile.set(rootProject.layout.projectDirectory.file("LICENSE"))
}

androidComponents {
    onVariants { variant ->
        variant.sources.assets?.addGeneratedSourceDirectory(copyLicense, CopyLicenseTask::outputDir)
    }
}
