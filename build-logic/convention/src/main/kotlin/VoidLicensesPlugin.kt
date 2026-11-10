import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import com.mikepenz.aboutlibraries.plugin.StrictMode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Generates the dependency-license data shown by `:core:licenses` and fails the build
 * when a runtime dependency is not under a permissive license (CLAUDE.md §5).
 */
class VoidLicensesPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.mikepenz.aboutlibraries.plugin.android")

        extensions.configure<AboutLibrariesExtension> {
            // Never fetch license data from the network: results must be reproducible.
            offlineMode.set(true)
            license {
                strictMode.set(StrictMode.FAIL)
                mapLicensesToSpdx.set(true)
                allowedLicenses.addAll(ALLOWED_LICENSES)
            }
        }
    }

    companion object {
        /** SPDX identifiers allowed for runtime dependencies. Changes require approval. */
        val ALLOWED_LICENSES = setOf("Apache-2.0", "MIT", "BSD-2-Clause", "BSD-3-Clause", "ISC")
    }
}
