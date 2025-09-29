package com.jg.childmomentsnap.core.common.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@Composable
fun rememberCmsAppState(
    navController: NavHostController = rememberNavController()
) = remember(navController) {
    CmsAppState(navController)
}



@Stable
class CmsAppState(
    val nvaController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = nvaController
            .currentBackStackEntryAsState()
            .value?.destination

    var isLoading by mutableStateOf(false)

    companion object {
        private const val TAG = "CmsAppState"
    }
}