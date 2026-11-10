package io.github.romanfabio.voidapps.buildlogic

import org.gradle.api.Project
import java.io.File
import java.util.Properties

/** Release signing material for one app (docs/SECURITY.md §1). */
internal data class ReleaseSigning(
    val storeFile: File,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String,
)

/**
 * Returns the signing material for this app, or null when `keystores/<app>.jks` or its
 * entries in `keystores/keystore.properties` are missing (e.g. on CI).
 */
internal fun Project.findReleaseSigning(): ReleaseSigning? {
    val keystoresDir = rootProject.layout.projectDirectory.dir("keystores")
    val storeFile = keystoresDir.file("$appId.jks").asFile
    val propsText = providers.fileContents(keystoresDir.file("keystore.properties")).asText.orNull
    if (!storeFile.isFile || propsText == null) return null
    val props = Properties().apply { load(propsText.reader()) }
    val storePassword = props.getProperty("$appId.storePassword") ?: return null
    val keyPassword = props.getProperty("$appId.keyPassword") ?: storePassword
    return ReleaseSigning(storeFile, storePassword, keyAlias = appId, keyPassword = keyPassword)
}
