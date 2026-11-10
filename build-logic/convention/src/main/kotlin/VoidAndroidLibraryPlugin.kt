import com.android.build.api.dsl.LibraryExtension
import io.github.romanfabio.voidapps.buildlogic.VoidSdk
import io.github.romanfabio.voidapps.buildlogic.configureAndroidLint
import io.github.romanfabio.voidapps.buildlogic.configureKotlinJvmTarget
import io.github.romanfabio.voidapps.buildlogic.configureUnitTests
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Convention for every `:core:<module>` library. minSdk is the core floor. */
class VoidAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        pluginManager.apply("void.quality")

        extensions.configure<LibraryExtension> {
            compileSdk = VoidSdk.COMPILE_SDK

            defaultConfig {
                minSdk = VoidSdk.CORE_MIN_SDK
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
            }

            compileOptions {
                sourceCompatibility = VoidSdk.JAVA_VERSION
                targetCompatibility = VoidSdk.JAVA_VERSION
            }

            testOptions {
                targetSdk = VoidSdk.TARGET_SDK
                unitTests.isIncludeAndroidResources = true
            }

            lint.targetSdk = VoidSdk.TARGET_SDK
            configureAndroidLint(lint)
        }

        configureKotlinJvmTarget()
        configureUnitTests()
    }
}
