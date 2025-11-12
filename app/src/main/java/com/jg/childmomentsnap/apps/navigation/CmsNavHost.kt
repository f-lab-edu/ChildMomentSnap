package com.jg.childmomentsnap.apps.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.moment.navigation.CAMERA_GRAPH
import com.jg.childmomentsnap.feature.moment.navigation.cameraGraph

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CmsNavHost(
    appState: CmsAppState,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = CAMERA_GRAPH,
            modifier = modifier
        ) {
            //   TODO calendar 모듈 완성되면 연결.
           // homeGraph(appState)
            cameraGraph(appState)
        }
    }
}