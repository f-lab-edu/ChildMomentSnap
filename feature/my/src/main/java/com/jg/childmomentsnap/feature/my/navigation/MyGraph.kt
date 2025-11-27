package com.jg.childmomentsnap.feature.my.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jg.childmomentsnap.core.ui.component.home.HomeRoute
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.my.screen.MyRoute
import kotlinx.serialization.Serializable

@Serializable
data object MyGraph

@Serializable
data object MyScreenRoute

const val MY_GRAPH = "my_graph"

private const val MY_ROUTE = "my_route"

fun NavHostController.navigateToMyGraph(navOptions: NavOptions? = null) {
    this.navigate(MyGraph, navOptions)
}


fun NavHostController.navigateToMyGraphXML(navOptions: NavOptions? = null) {
    this.navigate(MY_GRAPH, navOptions)
}

fun NavGraphBuilder.myGraph(
    appState: CmsAppState,
    useCompose: Boolean = true
) {
    val navController = appState.navController

    if (useCompose) {
        // HomeRoute.My로 직접 연결
        composable<HomeRoute.My> {
            MyRoute()
        }
    } else {
        // XML Navigation
        navigation(
            route = MY_GRAPH,
            startDestination = MY_ROUTE
        ) {
            composable(route = MY_ROUTE) {
                MyRoute()
            }
        }
    }
}