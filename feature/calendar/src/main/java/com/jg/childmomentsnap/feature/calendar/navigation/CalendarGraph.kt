package com.jg.childmomentsnap.feature.calendar.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jg.childmomentsnap.core.ui.component.home.HomeRoute
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.calendar.screen.CalendarRoute
import kotlinx.serialization.Serializable


@Serializable
data object CalendarGraph

@Serializable
data object CalendarScreenRoute


const val CALENDAR_GRAPH = "calendar_graph"

private const val CALENDAR_ROUTE = "calendar_route"

fun NavHostController.navigateToCalendarGraph(navOptions: NavOptions? = null) {
    this.navigate(CalendarGraph, navOptions)
}

fun NavHostController.navigateToCalendarGraphXML(navOptions: NavOptions? = null) {
    this.navigate(CALENDAR_GRAPH, navOptions)
}

fun NavGraphBuilder.calendarGraph(
    appState: CmsAppState,
    useCompose: Boolean = true
) {
    val navController = appState.navController

    if (useCompose) {
        composable<HomeRoute.Calendar> {
            CalendarRoute()
        }
    } else {
        //   XML Navigation
        navigation(
            route = CALENDAR_GRAPH,
            startDestination = CALENDAR_ROUTE
        ) {
            composable(route = CALENDAR_ROUTE) {
                CalendarRoute()
            }
        }
    }
}
