package io.github.romanfabio.voidapps.buildlogic

import com.android.build.api.dsl.Lint

/** Shared Android Lint rules: warnings are errors (docs/TESTING.md §6). */
internal fun configureAndroidLint(lint: Lint) {
    lint.apply {
        abortOnError = true
        warningsAsErrors = true
        checkDependencies = false
        error += setOf("NewApi", "ObsoleteSdkInt")
        // Version-freshness checks query the network and fail builds unpredictably;
        // dependency updates are handled by Dependabot (docs/CICD.md §3).
        disable += setOf(
            "GradleDependency",
            "NewerVersionAvailable",
            "AndroidGradlePluginVersion",
            "OldTargetApi",
            // Raised when a library's bundled lint checks were built for another lint version;
            // it reports a toolchain mismatch, not a problem in our code.
            "ObsoleteLintCustomCheck",
        )
    }
}
