package io.github.romanfabio.voidapps.template.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.github.romanfabio.voidapps.core.licenses.ui.aboutLicensesScreen
import io.github.romanfabio.voidapps.core.licenses.ui.navigateToAboutLicenses
import io.github.romanfabio.voidapps.template.R
import io.github.romanfabio.voidapps.template.feature.home.ui.HomeDestination
import io.github.romanfabio.voidapps.template.feature.home.ui.homeScreen

/** The only place that knows every destination of the app (docs/ARCHITECTURE.md §9). */
@Composable
fun TemplateNavHost(horizontalPadding: Dp, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeDestination, modifier = modifier) {
        homeScreen(
            horizontalPadding = horizontalPadding,
            onOpenAbout = { navController.navigateToAboutLicenses(R.raw.aboutlibraries) },
        )
        aboutLicensesScreen(onNavigateUp = navController::navigateUp)
    }
}
