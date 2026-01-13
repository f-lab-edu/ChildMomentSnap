package com.jg.childmomentsnap.feature.diary.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jg.childmomentsnap.core.ui.component.home.HomeRoute
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.diary.screen.DiaryRoute
import kotlinx.serialization.Serializable

@Serializable
data object DiaryGraph

@Serializable
data object DiaryScreenRoute

@Serializable
data class DiaryDetailRoute(val id: Long)

@Serializable
data class DiaryWriteRoute(val date: String)

const val DIARY_GRAPH = "diary_graph"

private const val DIARY_ROUTE = "diary_route"

fun NavHostController.navigateToDiaryGraph(navOptions: NavOptions? = null) {
    this.navigate(DiaryGraph, navOptions)
}

fun NavHostController.navigationToMomentGraphXML(navOptions: NavOptions? = null) {
    this.navigate(DIARY_GRAPH, navOptions)
}

fun NavGraphBuilder.diaryGraph(
    appState: CmsAppState,
    useCompose: Boolean = true
) {
    val navController = appState.navController

    if (useCompose) {
        composable<HomeRoute.Daily> {
            DiaryRoute()
        }
    } else {
        // XML Navigation
        navigation(
            route = DIARY_GRAPH,
            startDestination = DIARY_ROUTE
        ) {
            composable(route = DIARY_ROUTE) {
                DiaryRoute()
            }
        }
    }
}