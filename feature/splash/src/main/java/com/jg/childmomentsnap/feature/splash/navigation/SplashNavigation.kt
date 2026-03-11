package com.jg.childmomentsnap.feature.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.splash.screen.SplashRoute
import kotlinx.serialization.Serializable

@Serializable
data object SplashGraph

@Serializable
data object SplashRoute

fun NavGraphBuilder.splashGraph(
    appState: CmsAppState,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit
) {
   composable<SplashGraph> {
       SplashRoute(
           onNavigateToHome = onNavigateToHome,
           onNavigateToOnboarding = onNavigateToOnboarding
       )
   }
}