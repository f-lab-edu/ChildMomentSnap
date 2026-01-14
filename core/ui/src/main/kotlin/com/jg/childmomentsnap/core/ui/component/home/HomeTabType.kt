package com.jg.childmomentsnap.core.ui.component.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jg.childmomentsnap.core.ui.R
import kotlinx.serialization.Serializable

@Serializable
sealed class HomeRoute {
    @Serializable
    data object Feed : HomeRoute()
    
    @Serializable
    data object Moment : HomeRoute()
    
    @Serializable
    data object Daily : HomeRoute()
    
    @Serializable
    data object My : HomeRoute()
}

enum class HomeTabType(
    val typeSafeRoute: HomeRoute,
    val xmlRoute: String,
    val graph: String,
    @DrawableRes val selectedIconId: Int,
    @DrawableRes val unselectedIconId: Int,
    @StringRes val iconTextId: Int
) {
    FEED(
        typeSafeRoute = HomeRoute.Feed,
        xmlRoute = "feed/home_route",
        graph = "feed/graph",
        selectedIconId = R.drawable.ic_calendar_filled,
        unselectedIconId = R.drawable.ic_calendar,
        iconTextId = R.string.bottom_calendar
    ),

    MOMENT(
        typeSafeRoute = HomeRoute.Moment,
        xmlRoute = "moment/home_route",
        graph = "moment/graph",
        selectedIconId = R.drawable.ic_camera_filled,
        unselectedIconId = R.drawable.ic_camera,
        iconTextId = R.string.bottom_camera
    ),

    DAILY(
        typeSafeRoute = HomeRoute.Daily,
        xmlRoute = "daily/home_route",
        graph = "daily/graph",
        selectedIconId = R.drawable.ic_write_filled,
        unselectedIconId = R.drawable.ic_write,
        iconTextId = R.string.bottom_daily
    ),

    MY(
        typeSafeRoute = HomeRoute.My,
        xmlRoute = "my/home_route",
        graph = "my/graph",
        selectedIconId = R.drawable.ic_my_filled,
        unselectedIconId = R.drawable.ic_my,
        iconTextId = R.string.bottom_my
    );

    companion object {
        fun findHome(route: HomeRoute?): HomeTabType {
            return route?.let { homeRoute ->
                entries.firstOrNull { it.typeSafeRoute == homeRoute }
            } ?: FEED
        }

        fun findHome(value: String?): HomeTabType {
            return if(!value.isNullOrEmpty()) {
                entries.firstOrNull{ value.startsWith(it.xmlRoute) } ?: FEED
            } else {
                FEED
            }
        }

        fun isFeed(route: HomeRoute?): Boolean {
            return route == HomeRoute.Feed
        }
    }
}