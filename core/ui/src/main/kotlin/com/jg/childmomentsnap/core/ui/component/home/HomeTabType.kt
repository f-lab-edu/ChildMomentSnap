package com.jg.childmomentsnap.core.ui.component.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jg.childmomentsnap.core.ui.R

private const val CALENDAR_PREFIX = "calendar"
private const val CAMERA_PREFIX = "camera"
private const val DAILY_PREFIX = "daily"
private const val MY_PREFIX = "my"

enum class HomeTabType(
    val prefix: String,
    val graph: String,
    val route: String,
    @DrawableRes val selectedIconId: Int,
    @DrawableRes val unselectedIconId: Int,
    @StringRes val iconTextId: Int
) {
    CALENDAR(
        prefix = CALENDAR_PREFIX,
        graph = buildGraph(CALENDAR_PREFIX),
        route = buildHomeRoute(CALENDAR_PREFIX),
        selectedIconId = R.drawable.ic_calendar_filled,
        unselectedIconId = R.drawable.ic_calendar,
        iconTextId = R.string.bottom_calendar
    ),

    CAMERA(
        prefix = CAMERA_PREFIX,
        graph = buildGraph(CAMERA_PREFIX),
        route = buildHomeRoute(CAMERA_PREFIX),
        selectedIconId = R.drawable.ic_camera_filled,
        unselectedIconId = R.drawable .ic_camera,
        iconTextId = R.string.bottom_camera
    ),

    DAILY(
        prefix = DAILY_PREFIX,
        graph = buildGraph(DAILY_PREFIX),
        route = buildHomeRoute(DAILY_PREFIX),
        selectedIconId = R.drawable.ic_write_filled,
        unselectedIconId = R.drawable.ic_write,
        iconTextId = R.string.bottom_daily
    ),

    MY(
        prefix = MY_PREFIX,
        graph = buildGraph(MY_PREFIX),
        route = buildHomeRoute(MY_PREFIX),
        selectedIconId = R.drawable.ic_my_filled,
        unselectedIconId = R.drawable.ic_my,
        iconTextId = R.string.bottom_my
    );

    companion object {
        const val SEPARATOR = "-"
        const val GRAPH = "graph"
        const val HOME_ROUTE = "home_route"

        fun HomeTabType.buildRoute(value: String): String = prefix + SEPARATOR + value
        fun findHome(value: String?): HomeTabType {
            return if(!value.isNullOrEmpty()) {
                entries.firstOrNull{ value.startsWith(it.prefix) } ?: CALENDAR
            } else {
                CALENDAR
            }
        }

        fun isCalendar(value: String?): Boolean {
            return CALENDAR.route == value
        }
    }
}

private fun buildGraph(prefix: String): String = prefix + HomeTabType.SEPARATOR + HomeTabType.GRAPH
private fun buildHomeRoute(prefix: String): String = prefix + HomeTabType.SEPARATOR + HomeTabType.HOME_ROUTE