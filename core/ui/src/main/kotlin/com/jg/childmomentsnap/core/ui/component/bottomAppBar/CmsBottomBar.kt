package com.jg.childmomentsnap.core.ui.component.bottomAppBar

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jg.childmomentsnap.core.ui.component.home.HomeTabType
import com.jg.childmomentsnap.core.ui.util.extension.matchesTab

@Composable
fun CmsBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeTabs: List<HomeTabType> = HomeTabType.entries,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

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
                        tint = Color.Unspecified,
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


@Composable
@Deprecated("Use CmsBottomBar with useTypeSafe parameter instead")
fun CmsBottomBarLegacy(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeTaps: List<HomeTabType> = HomeTabType.entries
) {
    CmsBottomBar(
        modifier = modifier,
        navController = navController,
        homeTabs = homeTaps,
    )
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