package com.jg.childmomentsnap.apps.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jg.childmomentsnap.apps.navigation.CmsNavHost
import com.jg.childmomentsnap.core.ui.component.bottomAppBar.CmsBottomBar
import com.jg.childmomentsnap.core.ui.component.home.HomeRoute
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.core.ui.state.rememberCmsAppState

@Composable
fun CmsApp(
    modifier: Modifier = Modifier,
    appState: CmsAppState = rememberCmsAppState()
) {


    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        CmsNavHost(appState = appState)

        CmsBottomBar(
            navController = appState.navController,
            onFabClick = {
                appState.navController.navigate(HomeRoute.Moment)
            }
        )
    }
}