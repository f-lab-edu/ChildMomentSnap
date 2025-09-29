package com.jg.childmomentsnap.apps.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.jg.childmomentsnap.core.common.state.CmsAppState
import com.jg.childmomentsnap.feature.photo.navigation.CAMERA_GRAPH
import com.jg.childmomentsnap.feature.photo.navigation.cameraGraph

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CmsNavHost(
    appState: CmsAppState,
    modifier: Modifier = Modifier
) {
    val navController = appState.nvaController

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = CAMERA_GRAPH,
            modifier = modifier
        ) {
            cameraGraph(appState)
        }
    }
}