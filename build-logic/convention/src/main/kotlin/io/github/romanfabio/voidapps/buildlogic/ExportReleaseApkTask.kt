package io.github.romanfabio.voidapps.buildlogic

import com.android.build.api.variant.BuiltArtifactsLoader
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

/**
 * Copies the signed release APK to `build/outputs/void/void-<app>-<version>.apk` after
 * verifying its signature with apksigner (docs/CICD.md §4).
 */
abstract class ExportReleaseApkTask : DefaultTask() {
    @get:InputFiles
    abstract val apkFolder: DirectoryProperty

    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

    @get:Input
    abstract val signingConfigured: Property<Boolean>

    @get:Input
    abstract val apksignerPath: Property<String>

    @get:Input
    abstract val outputFileName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun export() {
        if (!signingConfigured.get()) {
            throw GradleException(
                "Release signing is not configured. Expected keystores/<app>.jks and its entries in " +
                    "keystores/keystore.properties (docs/SECURITY.md §1).",
            )
        }
        val artifacts = builtArtifactsLoader.get().load(apkFolder.get())
            ?: throw GradleException("Cannot load release APK from ${apkFolder.get()}")
        val apk = File(artifacts.elements.single().outputFile)

        execOperations.exec { commandLine(apksignerPath.get(), "verify", "--print-certs", apk.absolutePath) }

        val target = outputDir.get().file(outputFileName.get()).asFile
        apk.copyTo(target, overwrite = true)
        logger.lifecycle("Signed release APK: ${target.absolutePath}")
    }
}
