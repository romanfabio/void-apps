plugins {
    alias(libs.plugins.void.android.library)
}

android {
    namespace = "io.github.romanfabio.voidapps.core.testing"
}

dependencies {
    api(projects.core.common)
    api(libs.junit)
    api(libs.kotlin.test)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
}
