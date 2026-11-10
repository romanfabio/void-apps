plugins {
    alias(libs.plugins.void.android.library)
    alias(libs.plugins.void.compose)
}

android {
    namespace = "io.github.romanfabio.voidapps.core.designsystem"
}

dependencies {
    testImplementation(libs.androidx.test.core)
}
