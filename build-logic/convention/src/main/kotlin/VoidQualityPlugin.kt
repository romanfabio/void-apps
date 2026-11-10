import com.diffplug.gradle.spotless.SpotlessExtension
import io.github.romanfabio.voidapps.buildlogic.VoidSdk
import io.github.romanfabio.voidapps.buildlogic.isApp
import io.github.romanfabio.voidapps.buildlogic.lib
import io.github.romanfabio.voidapps.buildlogic.libs
import io.github.romanfabio.voidapps.buildlogic.version
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/**
 * Formatting (Spotless + ktlint), static analysis (detekt) and coverage (Kover).
 * Thresholds are defined in docs/TESTING.md §5; changing them requires approval.
 */
class VoidQualityPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        configureSpotless()
        configureDetekt()
        configureKover()
        dependencies {
            add("testImplementation", libs.lib("junit"))
            add("testImplementation", libs.lib("kotlin-test"))
        }
    }

    private fun Project.configureSpotless() {
        pluginManager.apply("com.diffplug.spotless")
        val ktlintVersion = libs.version("ktlint")
        val editorConfig = rootProject.file(".editorconfig")
        extensions.configure<SpotlessExtension> {
            kotlin {
                target("src/**/*.kt")
                ktlint(ktlintVersion).setEditorConfigPath(editorConfig)
            }
            kotlinGradle {
                target("*.gradle.kts")
                ktlint(ktlintVersion).setEditorConfigPath(editorConfig)
            }
        }
    }

    private fun Project.configureDetekt() {
        pluginManager.apply("io.gitlab.arturbosch.detekt")
        extensions.configure<DetektExtension> {
            buildUponDefaultConfig = true
            parallel = true
            config.setFrom(rootProject.file("config/detekt/detekt.yml"))
            source.setFrom("src/main/java", "src/test/java", "src/androidTest/java")
        }
        tasks.withType<Detekt>().configureEach {
            jvmTarget = VoidSdk.JVM_TARGET.toString()
        }
    }

    private fun Project.configureKover() {
        pluginManager.apply("org.jetbrains.kotlinx.kover")
        val coverageRule: CoverageRule = when {
            isApp -> CoverageRule.App
            path == ":core:common" -> CoverageRule.WholeModule
            else -> CoverageRule.None
        }
        extensions.configure<KoverProjectExtension> {
            reports {
                filters {
                    excludes {
                        annotatedBy(
                            "androidx.compose.runtime.Composable",
                            "androidx.compose.ui.tooling.preview.Preview",
                            "dagger.Module",
                        )
                        classes(
                            "*Hilt_*",
                            "hilt_aggregated_deps.*",
                            "dagger.hilt.internal.*",
                            "*_GeneratedInjector",
                            "*_ComponentTreeDeps",
                            "*_HiltComponents*",
                            "*Module_*Factory",
                            "*Module_*Factory\$*",
                            "*_Factory",
                            "*_Factory\$*",
                            "*_HiltModules*",
                            "*_MembersInjector",
                            "*_Impl",
                            "*_Impl\$*",
                            "*ComposableSingletons*",
                            "*.BuildConfig",
                            "*.R",
                            "*.R\$*",
                        )
                    }
                    if (coverageRule == CoverageRule.App) {
                        includes {
                            classes(
                                "*ViewModel",
                                "*ViewModel\$*",
                                "*UseCase",
                                "*UseCase\$*",
                                "*.Default*Repository",
                                "*.Default*Repository\$*",
                            )
                        }
                    }
                }
                if (coverageRule != CoverageRule.None) {
                    verify {
                        rule("Minimum line coverage") {
                            minBound(MIN_LINE_COVERAGE)
                        }
                    }
                }
            }
        }
    }

    private enum class CoverageRule { App, WholeModule, None }

    private companion object {
        const val MIN_LINE_COVERAGE = 80
    }
}
