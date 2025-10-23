package com.jg.childmomentsnap.feature.photo.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.photo.screen.CameraRoute


/**
 * 카메라 네비게이션 그래프 경로
 */
const val CAMERA_GRAPH = "camera_graph"

/**
 * 카메라 화면 경로
 */
private const val CAMERA_ROUTE = "camera_route"

/**
 * 카메라 그래프로 이동하는 확장 함수
 * 
 * @param navOptions 네비게이션 옵션
 */
fun NavHostController.navigateToCameraGraph(navOptions: NavOptions? = null) {
    this.navigate(CAMERA_GRAPH, navOptions)
}

/**
 * 카메라 네비게이션 그래프를 NavGraphBuilder에 추가
 * 
 * 카메라 관련 모든 화면들을 포함하는 중첩 네비게이션 그래프를 설정합니다.
 * 
 * @param appState 앱 상태 관리 객체
 */
fun NavGraphBuilder.cameraGraph(
    appState: CmsAppState
) {
    val navController = appState.nvaController

    navigation(
        route = CAMERA_GRAPH,
        startDestination = CAMERA_ROUTE
    ) {
        composable(route = CAMERA_ROUTE) {
            CameraRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateUp = {
                    navController.navigateUp()
                }
            )
        }
    }
}