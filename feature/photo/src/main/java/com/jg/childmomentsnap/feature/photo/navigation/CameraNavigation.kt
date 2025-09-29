package com.jg.childmomentsnap.feature.photo.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jg.childmomentsnap.core.common.state.CmsAppState
import com.jg.childmomentsnap.feature.photo.CameraRoute

const val CAMERA_GRAPH = "camera_graph"

private const val CAMERA_ROUTE = "camera_route"

fun NavHostController.navigateToCameraGraph(navOptions: NavOptions? = null) {
    this.navigate(CAMERA_GRAPH, navOptions)
}


fun NavGraphBuilder.cameraGraph(
    appState: CmsAppState
) {
    val navController = appState.nvaController

    navigation(
        route = CAMERA_GRAPH,
        startDestination = CAMERA_ROUTE
    ) {
        composable(route = CAMERA_ROUTE) {
            CameraRoute()
        }
    }
}