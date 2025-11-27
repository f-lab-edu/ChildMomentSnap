package com.jg.childmomentsnap.core.ui.component.bottomAppBar

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jg.childmomentsnap.core.ui.component.home.HomeTabType
import com.jg.childmomentsnap.core.ui.util.extension.matchesTab
import com.jg.childmomentsnap.core.ui.util.extension.shouldShowBottomBar

@Composable
fun CmsBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeTabs: List<HomeTabType> = HomeTabType.entries,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val shouldShowBottomBar by remember(navBackStackEntry) {
        derivedStateOf {
            navBackStackEntry?.destination?.shouldShowBottomBar(homeTabs) ?: false
        }
    }

    if (shouldShowBottomBar) {
        BottomAppBar(modifier = modifier) {
            homeTabs.forEach { tab ->
                val selected = navBackStackEntry?.destination.matchesTab(tab)

                NavigationBarItem(
                    selected = selected,
                    icon = {
                        Icon(
                            painter = if (selected) {
                                painterResource(id = tab.selectedIconId)
                            } else {
                                painterResource(id = tab.unselectedIconId)
                            },
                            contentDescription = stringResource(tab.iconTextId)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(tab.iconTextId),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {
                        if (!selected) {
                            navigateToHomeTab(
                                navController = navController,
                                tab = tab
                            )
                        }
                    }
                )
            }
        }
    }

}

private fun navigateToHomeTab(
    navController: NavHostController,
    tab: HomeTabType,
) {
    navController.navigate(tab.typeSafeRoute) {
        // 현재 백스택의 첫 번째 목적지까지 팝업하여 중복 방지
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}