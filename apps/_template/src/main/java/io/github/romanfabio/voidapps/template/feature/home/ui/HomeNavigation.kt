package io.github.romanfabio.voidapps.template.feature.home.ui

import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination

fun NavGraphBuilder.homeScreen(horizontalPadding: Dp, onOpenAbout: () -> Unit) {
    composable<HomeDestination> {
        HomeRoute(horizontalPadding = horizontalPadding, onOpenAbout = onOpenAbout)
    }
}

fun NavController.navigateToHome() {
    navigate(HomeDestination)
}
