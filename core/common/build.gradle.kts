plugins {
    alias(libs.plugins.void.android.library)
    alias(libs.plugins.void.hilt)
}

android {
    namespace = "io.github.romanfabio.voidapps.core.common"
}

dependencies {
    api(libs.kotlinx.coroutines.android)

    testImplementation(libs.kotlinx.coroutines.test)
}
