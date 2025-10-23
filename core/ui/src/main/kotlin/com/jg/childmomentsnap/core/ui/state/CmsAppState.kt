package com.jg.childmomentsnap.core.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jg.childmomentsnap.core.ui.component.home.HomeTabType

/**
 *  App 상태를 전달해야 하는 경우 대비
 *  추후 추가
 */

@Composable
fun rememberCmsAppState(
    navController: NavHostController
): CmsAppState {
    return remember {
        CmsAppState(navController)
    }
}

@Stable
class CmsAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isHomeLevelDestination: Boolean
        @Composable get() = HomeTabType.entries
            .map { it.route }
            .contains(currentDestination?.route ?: "")
}