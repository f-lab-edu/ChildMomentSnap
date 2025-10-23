package com.jg.childmomentsnap.core.ui.util.extension

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.jg.childmomentsnap.core.ui.component.home.HomeTabType

fun NavDestination?.isHomeLevelTab(tab: HomeTabType) =
    this?.hierarchy?.any {
        it.route?.contains(tab.route, true) ?: false
    } ?: false

fun NavDestination?.shouldShowBottomBar(homeLevelTabs: List<HomeTabType>): Boolean {
    return homeLevelTabs.any { tab ->
        this.isHomeLevelTab(tab)
    }
}