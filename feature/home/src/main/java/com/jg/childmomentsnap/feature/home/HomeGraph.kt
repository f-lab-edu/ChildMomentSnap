package com.jg.childmomentsnap.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.jg.childmomentsnap.core.ui.component.home.HomeTabType
import com.jg.childmomentsnap.core.ui.state.CmsAppState

private const val HOME_GRAPH = "home_graph"

fun NavHostController.navigateToHomeGraph(
    navOptions: NavOptions? = null
) {
    this.navigate(HOME_GRAPH, navOptions)
}

fun NavGraphBuilder.homeGraph(
    cmsAppState: CmsAppState,
    navController: NavHostController,
    onBackPressed: () -> Unit
) {
    navigation(route = HOME_GRAPH, startDestination = HomeTabType.CALENDAR.route) {
        //  Calendar Graph
        //  Camera Graph
        //  Daily Graph
        //  My Graph
    }
}