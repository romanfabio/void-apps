import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import io.github.romanfabio.voidapps.buildlogic.ExportReleaseApkTask
import io.github.romanfabio.voidapps.buildlogic.VoidSdk
import io.github.romanfabio.voidapps.buildlogic.appId
import io.github.romanfabio.voidapps.buildlogic.configureAndroidLint
import io.github.romanfabio.voidapps.buildlogic.configureKotlinJvmTarget
import io.github.romanfabio.voidapps.buildlogic.configureUnitTests
import io.github.romanfabio.voidapps.buildlogic.findReleaseSigning
import io.github.romanfabio.voidapps.buildlogic.readAppVersion
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

/**
 * Convention for every `:apps:<app>` module. See docs/ARCHITECTURE.md §3.
 * Apps set only `namespace`, `applicationId` and, optionally, `minSdk`.
 */
class VoidAndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("void.quality")

        val version = readAppVersion()
        val signing = findReleaseSigning()

        extensions.configure<ApplicationExtension> {
            compileSdk = VoidSdk.COMPILE_SDK

            defaultConfig {
                minSdk = VoidSdk.DEFAULT_APP_MIN_SDK
                targetSdk = VoidSdk.TARGET_SDK
                versionCode = version.code
                versionName = version.name
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions {
                sourceCompatibility = VoidSdk.JAVA_VERSION
                targetCompatibility = VoidSdk.JAVA_VERSION
            }

            if (signing != null) {
                signingConfigs.create("release") {
                    storeFile = signing.storeFile
                    storePassword = signing.storePassword
                    keyAlias = signing.keyAlias
                    keyPassword = signing.keyPassword
                }
            }

            buildTypes {
                getByName("debug") {
                    applicationIdSuffix = ".debug"
                }
                getByName("release") {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    isDebuggable = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        rootProject.file("config/proguard/void-rules.pro"),
                        "proguard-rules.pro",
                    )
                    signingConfig = signingConfigs.findByName("release")
                }
            }

            testOptions {
                unitTests.isIncludeAndroidResources = true
            }

            packaging {
                resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }

            configureAndroidLint(lint)
        }

        extensions.configure<ApplicationAndroidComponentsExtension> {
            finalizeDsl { android ->
                val minSdk = android.defaultConfig.minSdk ?: VoidSdk.DEFAULT_APP_MIN_SDK
                if (minSdk !in VoidSdk.CORE_MIN_SDK..VoidSdk.TARGET_SDK) {
                    throw GradleException(
                        "$path: minSdk $minSdk is outside ${VoidSdk.CORE_MIN_SDK}..${VoidSdk.TARGET_SDK} " +
                            "(docs/ANDROID_SUPPORT.md §3)",
                    )
                }
                if (android.namespace != android.defaultConfig.applicationId) {
                    throw GradleException("$path: applicationId must equal namespace (docs/NAMING.md §1)")
                }
            }

            val apksigner = sdkComponents.sdkDirectory.map { sdk ->
                val buildTools = sdk.dir("build-tools").asFile.listFiles()
                    ?.filter { it.isDirectory }
                    ?.maxByOrNull { it.name }
                    ?: throw GradleException("No Android build-tools installed in ${sdk.asFile}")
                val exe = if (System.getProperty("os.name").startsWith("Windows")) "apksigner.bat" else "apksigner"
                buildTools.resolve(exe).absolutePath
            }

            onVariants(selector().withBuildType("release")) { variant ->
                tasks.register<ExportReleaseApkTask>("exportReleaseApk") {
                    group = "void"
                    description = "Builds, verifies and exports the signed release APK."
                    apkFolder.set(variant.artifacts.get(SingleArtifact.APK))
                    builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
                    signingConfigured.set(signing != null)
                    apksignerPath.set(apksigner)
                    outputFileName.set("void-$appId-${version.name}.apk")
                    outputDir.set(layout.buildDirectory.dir("outputs/void"))
                }
            }
        }

        configureKotlinJvmTarget()
        configureUnitTests()
    }
}
