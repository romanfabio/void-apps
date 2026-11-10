package io.github.romanfabio.voidapps.core.licenses.ui

import androidx.annotation.RawRes
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * Destination of the mandatory About & licenses screen (docs/DESIGN_SYSTEM.md §8).
 * [librariesRes] is the app's generated `R.raw.aboutlibraries`.
 */
@Serializable
data class AboutLicensesDestination(@param:RawRes val librariesRes: Int) {
    companion object {
        /** SavedStateHandle key; must match the property name above. */
        const val ARG_LIBRARIES_RES = "librariesRes"
    }
}

fun NavGraphBuilder.aboutLicensesScreen(onNavigateUp: () -> Unit) {
    composable<AboutLicensesDestination> {
        AboutLicensesRoute(onNavigateUp = onNavigateUp)
    }
}

fun NavController.navigateToAboutLicenses(@RawRes librariesRes: Int) {
    navigate(AboutLicensesDestination(librariesRes))
}
