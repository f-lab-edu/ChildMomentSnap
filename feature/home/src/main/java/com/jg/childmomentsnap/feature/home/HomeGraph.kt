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