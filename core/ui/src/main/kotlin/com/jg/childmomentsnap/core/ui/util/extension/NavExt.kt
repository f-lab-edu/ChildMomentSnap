package com.jg.childmomentsnap.core.ui.util.extension

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.jg.childmomentsnap.core.ui.component.home.HomeRoute
import com.jg.childmomentsnap.core.ui.component.home.HomeTabType



fun NavDestination?.isHomeLevelTab(tab: HomeTabType): Boolean =
    this?.hierarchy?.any {
        it.route?.contains(tab.xmlRoute, true) ?: false
    } ?: false



fun NavDestination?.isHomeLevelTab(route: HomeRoute): Boolean =
    this?.hierarchy?.any {
        it.route == route::class.qualifiedName
    } ?: false

fun NavDestination?.shouldShowBottomBar(homeTabTypes: List<HomeTabType>): Boolean {
    return homeTabTypes.any { tab ->
         this.isHomeLevelTab(tab.typeSafeRoute)
    }
}

fun NavDestination?.matchesTab(tab: HomeTabType): Boolean =
    isHomeLevelTab(tab.typeSafeRoute) || isHomeLevelTab(tab)

/**
 * 현재 선택된 홈 탭 찾기
 */
fun NavDestination?.getCurrentHomeTab(): HomeTabType? {
    return HomeTabType.entries.firstOrNull { tab ->
        matchesTab(tab)
    }
}

/**
 * 특정 라우트가 홈 레벨인지 확인
 */
fun NavDestination?.isHomeLevel(): Boolean {
    return HomeTabType.entries.any { tab ->
        matchesTab(tab)
    }
}