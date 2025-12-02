package com.jg.childmomentsnap.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.jg.childmomentsnap.core.ui.component.home.HomeRoute
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.calendar.navigation.calendarGraph
import com.jg.childmomentsnap.feature.diary.navigation.diaryGraph
import com.jg.childmomentsnap.feature.moment.navigation.momentGraph
import com.jg.childmomentsnap.feature.my.navigation.myGraph
import kotlinx.serialization.Serializable


@Serializable
data object HomeGraphRoute

/**
 * XML 호환을 위한 상수
 */
private const val HOME_GRAPH = "home_graph"

fun NavHostController.navigateToHomeGraph(
    navOptions: NavOptions? = null
) {
    this.navigate(HomeGraphRoute, navOptions)
}

/**
 * XML Navigation으로 Home Graph 이동
 */
fun NavHostController.navigateToHomeGraphLegacy(
    navOptions: NavOptions? = null
) {
    this.navigate(HOME_GRAPH, navOptions)
}


fun NavGraphBuilder.homeGraph(
    cmsAppState: CmsAppState,
) {
    navigation<HomeGraphRoute>(
        startDestination = HomeRoute.Calendar
    ) {
        // Calendar Tab
        calendarGraph(
            appState = cmsAppState,
            useCompose = true
        )

        // Moment Tab
        momentGraph(
            appState = cmsAppState,
            useCompose = true
        )

        // Daily Tab
        diaryGraph(
            appState = cmsAppState,
            useCompose = true
        )

        // My Tab
        myGraph(
            appState = cmsAppState,
            useCompose = true
        )
    }
}