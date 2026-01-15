package com.jg.childmomentsnap.core.ui.component.bottomAppBar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jg.childmomentsnap.core.ui.component.home.HomeTabType
import com.jg.childmomentsnap.core.ui.theme.Amber500
import com.jg.childmomentsnap.core.ui.theme.Stone300
import com.jg.childmomentsnap.core.ui.theme.Stone900
import com.jg.childmomentsnap.core.ui.util.extension.matchesTab
import com.jg.childmomentsnap.core.ui.util.extension.shouldShowBottomBar

@Composable
fun CmsBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeTabs: List<HomeTabType> = HomeTabType.entries,
    onFabClick: () -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val shouldShowBottomBar by remember(navBackStackEntry) {
        derivedStateOf {
            navBackStackEntry?.destination?.shouldShowBottomBar(homeTabs) ?: false
        }
    }

    if (shouldShowBottomBar) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            // 하단 탭 영역
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(80.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentDestination = navBackStackEntry?.destination
                    
                    // Left Tab: Feed
                    homeTabs.find { it == HomeTabType.FEED }?.let { tab ->
                        val selected = currentDestination.matchesTab(tab)
                        CmsBottomNavItem(
                            iconId = if (selected) tab.selectedIconId else tab.unselectedIconId,
                            labelId = tab.iconTextId,
                            isSelected = selected,
                            onClick = {
                                if (!selected) navigateToHomeTab(navController, tab)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(60.dp)) // Center FAB Space

                    // Right Tab: My
                    homeTabs.find { it == HomeTabType.MY }?.let { tab ->
                        val selected = currentDestination.matchesTab(tab)
                        CmsBottomNavItem(
                            iconId = if (selected) tab.selectedIconId else tab.unselectedIconId,
                            labelId = tab.iconTextId,
                            isSelected = selected,
                            onClick = {
                                if (!selected) navigateToHomeTab(navController, tab)
                            }
                        )
                    }
                }
            }

            // 중앙 플로팅 액션 버튼 (FAB)
            FloatingActionButton(
                onClick = onFabClick,
                shape = CircleShape,
                containerColor = Stone900,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(64.dp)
                    .offset(y = (-12).dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "기록 추가",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun CmsBottomNavItem(
    iconId: Int, labelId: Int, isSelected: Boolean, onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = stringResource(id = labelId),
            tint = if (isSelected) Amber500 else Stone300,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(id = labelId),
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 10.sp),
            color = if (isSelected) Amber500 else Stone300
        )
    }
}

private fun navigateToHomeTab(
    navController: NavHostController,
    tab: HomeTabType,
) {
    navController.navigate(tab.typeSafeRoute) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}