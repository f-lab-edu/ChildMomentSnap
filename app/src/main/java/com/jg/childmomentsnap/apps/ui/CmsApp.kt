package com.jg.childmomentsnap.apps.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jg.childmomentsnap.apps.navigation.CmsNavHost
import com.jg.childmomentsnap.core.common.state.CmsAppState
import com.jg.childmomentsnap.core.common.state.rememberCmsAppState

@Composable
fun CmsApp(
    modifier: Modifier = Modifier,
    appState: CmsAppState = rememberCmsAppState()
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CmsNavHost(appState = appState)
    }
}