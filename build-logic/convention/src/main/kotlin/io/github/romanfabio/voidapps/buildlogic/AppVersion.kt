package io.github.romanfabio.voidapps.buildlogic

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.util.Properties

/** Version read from `apps/<app>/version.properties` (docs/NAMING.md §8). */
internal data class AppVersion(val major: Int, val minor: Int, val patch: Int) {
    val name: String get() = "$major.$minor.$patch"
    val code: Int get() = major * 1_000_000 + minor * 1_000 + patch
}

private val SEMVER = Regex("""^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)$""")
private const val MAX_COMPONENT = 999

internal fun Project.readAppVersion(): AppVersion {
    val file = layout.projectDirectory.file("version.properties")
    val text = providers.fileContents(file).asText.orNull
        ?: throw GradleException("$path: missing version.properties (docs/NAMING.md §8)")
    val raw = Properties().apply { load(text.reader()) }.getProperty("version")?.trim()
        ?: throw GradleException("$path: version.properties must contain 'version=MAJOR.MINOR.PATCH'")
    val match = SEMVER.matchEntire(raw)
        ?: throw GradleException("$path: version '$raw' is not MAJOR.MINOR.PATCH")
    val (major, minor, patch) = match.destructured.toList().map(String::toInt)
    if (minor > MAX_COMPONENT || patch > MAX_COMPONENT) {
        throw GradleException("$path: MINOR and PATCH must be <= $MAX_COMPONENT (got $raw)")
    }
    return AppVersion(major, minor, patch)
}
