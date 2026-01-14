package com.jg.childmomentsnap.feature.feed.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jg.childmomentsnap.core.ui.component.home.HomeRoute
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.feed.screen.FeedRoute
import kotlinx.serialization.Serializable


@Serializable
data object FeedGraph

@Serializable
data object FeedScreenRoute


const val FEED_GRAPH = "feed_graph"

private const val FEED_ROUTE = "feed_route"

fun NavHostController.navigateToFeedGraph(navOptions: NavOptions? = null) {
    this.navigate(FeedGraph, navOptions)
}

fun NavHostController.navigateToFeedGraphXML(navOptions: NavOptions? = null) {
    this.navigate(FEED_GRAPH, navOptions)
}

fun NavGraphBuilder.feedGraph(
    appState: CmsAppState,
    useCompose: Boolean = true
) {
    val navController = appState.navController

    if (useCompose) {
        composable<HomeRoute.Feed> { // TODO: Update to HomeRoute.Feed if changed in HomeRoute
            FeedRoute(
                onNavigateToDetail = { diaryId ->
                  //  navController.navigate(DiaryDetailRoute(diaryId))
                },
                onNavigateToWrite = { date ->
                   //  navController.navigate(DiaryWriteRoute(date.toString()))
                },
                onNavigateToCamera = { navController.navigate(HomeRoute.Moment) }
            )
        }
    } else {
        //   XML Navigation
        navigation(
            route = FEED_GRAPH,
            startDestination = FEED_ROUTE
        ) {
            composable(route = FEED_ROUTE) {
                FeedRoute(
                    onNavigateToDetail = { diaryId ->
                        // TODO
                    },
                    onNavigateToWrite = { date ->
                        //  TODO
                    },
                    onNavigateToCamera = {
                        //  TODO
                    }
                )
            }
        }
    }
}
