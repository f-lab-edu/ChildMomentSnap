package com.jg.childmomentsnap.core.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jg.childmomentsnap.core.ui.util.extension.isHomeLevel

/**
 *  App 상태를 전달해야 하는 경우 대비
 *  추후 추가
 */

@Composable
fun rememberCmsAppState(
    navController: NavHostController = rememberNavController()
) = remember(navController) {
    CmsAppState(navController)
}



@Stable
class CmsAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isHomeLevelDestination: Boolean
        @Composable get() = currentDestination.isHomeLevel()

    companion object {
        private const val TAG = "CmsAppState"
    }
}