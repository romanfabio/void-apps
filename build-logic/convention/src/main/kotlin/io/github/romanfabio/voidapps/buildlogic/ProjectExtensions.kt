package io.github.romanfabio.voidapps.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.lib(alias: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(alias).orElseThrow { IllegalArgumentException("Missing library '$alias' in libs.versions.toml") }

internal fun VersionCatalog.version(alias: String): String =
    findVersion(alias).orElseThrow { IllegalArgumentException("Missing version '$alias' in libs.versions.toml") }
        .requiredVersion

/** `apps/<app>` → `<app>`; `_template` stays `_template`. */
internal val Project.appId: String
    get() = name

internal val Project.isApp: Boolean
    get() = path.startsWith(":apps:")

internal fun Project.configureKotlinJvmTarget() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.fromTarget(VoidSdk.JVM_TARGET.toString()))
    }
}

/** Robolectric (SDK 36 sandbox) needs access to JDK internals on JDK 17+. */
internal fun Project.configureUnitTests() {
    tasks.withType<Test>().configureEach {
        jvmArgs(
            "--add-opens=java.base/java.io=ALL-UNNAMED",
            "--add-exports=java.base/jdk.internal.access=ALL-UNNAMED",
        )
    }
}
