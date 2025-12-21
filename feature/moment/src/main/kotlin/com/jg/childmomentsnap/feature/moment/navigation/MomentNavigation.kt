package com.jg.childmomentsnap.feature.moment.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jg.childmomentsnap.core.ui.component.home.HomeRoute
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.moment.screen.CameraRoute
import kotlinx.serialization.Serializable


@Serializable
data object MomentGraph


@Serializable
data object MomentScreenRoute


const val MOMENT_GRAPH = "moment_graph"


private const val MOMENT_ROUTE = "moment_route"


fun NavHostController.navigateToMomentGraph(navOptions: NavOptions? = null) {
    this.navigate(MomentGraph, navOptions)
}


fun NavHostController.navigateToMomentGraphXML(navOptions: NavOptions? = null) {
    this.navigate(MOMENT_GRAPH, navOptions)
}

fun NavGraphBuilder.momentGraph(
    appState: CmsAppState,
    useCompose: Boolean = true
) {
    val navController = appState.navController

    if (useCompose) {
        // HomeRoute.Moment로 직접 연결
        composable<HomeRoute.Moment> {
            CameraRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }
    } else {
        // XML Navigation
        navigation(
            route = MOMENT_GRAPH,
            startDestination = MOMENT_ROUTE
        ) {
            composable(route = MOMENT_ROUTE) {
                CameraRoute(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}