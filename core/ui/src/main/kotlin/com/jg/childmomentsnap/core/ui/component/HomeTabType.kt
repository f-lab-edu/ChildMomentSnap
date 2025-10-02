package com.jg.childmomentsnap.core.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

private const val CALENDAR_PREFIX = "calendar"
private const val DAILY_PREFIX = "daily"
private const val MY_PREFIX = "my"

enum class HomeTabType(
    val prefix: String,
    val graph: String,
    val route: String,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val iconTextId: Int
) {
}