package io.github.romanfabio.voidapps.buildlogic

import org.gradle.api.JavaVersion

/**
 * SDK levels for the whole repository. See docs/ANDROID_SUPPORT.md.
 * Changing any value requires owner approval (CLAUDE.md §6).
 */
object VoidSdk {
    const val COMPILE_SDK = 37
    const val TARGET_SDK = 37
    const val DEFAULT_APP_MIN_SDK = 28
    const val CORE_MIN_SDK = 26
    const val JVM_TARGET = 21
    val JAVA_VERSION: JavaVersion = JavaVersion.VERSION_21
}
