package com.jg.childmomentsnap.core.ui.component.bottomAppBar

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jg.childmomentsnap.core.ui.component.home.HomeTabType
import com.jg.childmomentsnap.core.ui.util.extension.ComposableVisibleState
import com.jg.childmomentsnap.core.ui.util.extension.isHomeLevelTab
import com.jg.childmomentsnap.core.ui.util.extension.shouldShowBottomBar

@Composable
fun CmsBottomBar(
    modifier: Modifier,
    navController: NavHostController,
    homeTaps: List<HomeTabType> = HomeTabType.entries
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    BottomAppBar {
        homeTaps.forEach { tab ->
            val selected = navBackStackEntry?.destination.isHomeLevelTab(tab)

            NavigationBarItem(
                selected = selected,
                icon = {
                    Icon(
                        if (selected) painterResource(id = tab.selectedIconId) else painterResource(id = tab.unselectedIconId),
                        tint = Color.Unspecified,
                        contentDescription = null
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
                        navController.navigate(tab.graph) {
                            popUpTo(HomeTabType.CALENDAR.route)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }

}