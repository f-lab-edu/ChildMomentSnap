package com.jg.childmomentsnap.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.onboarding.screen.OnboardingRoute
import kotlinx.serialization.Serializable

@Serializable
data object OnBoardingGraph

const val ONBOARDING_GRAPH = "onboarding_graph"

const val ONBOARDING_ROUTE = "onboarding_route"

fun NavGraphBuilder.onboardingGraph(
    appState: CmsAppState,
    onCompleted: () -> Unit
) {
    val navController = appState.navController

    composable<OnBoardingGraph> {
        OnboardingRoute(
            onCompleted = onCompleted
        )
    }
}