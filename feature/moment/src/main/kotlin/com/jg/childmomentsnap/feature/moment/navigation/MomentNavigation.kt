package com.jg.childmomentsnap.feature.moment.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.jg.childmomentsnap.core.model.VisionAnalysis
import com.jg.childmomentsnap.core.ui.component.home.HomeRoute
import com.jg.childmomentsnap.core.ui.state.CmsAppState
import com.jg.childmomentsnap.feature.moment.screen.CameraRoute
import com.jg.childmomentsnap.feature.moment.screen.RecordingRoute
import kotlinx.serialization.Serializable
import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf


@Serializable
data object MomentGraph


@Serializable
data object MomentScreenRoute


@Serializable
data class RecordingScreenRoute(
    val visionAnalysisContent: String,
    val imageUri: String,
    val visionAnalysis: VisionAnalysis
)


const val MOMENT_GRAPH = "moment_graph"


private const val MOMENT_ROUTE = "moment_route"


fun NavHostController.navigateToMomentGraph(navOptions: NavOptions? = null) {
    this.navigate(MomentGraph, navOptions)
}


fun NavHostController.navigateToMomentGraphXML(navOptions: NavOptions? = null) {
    this.navigate(MOMENT_GRAPH, navOptions)
}


private fun NavHostController.navigateToRecording(
    imageUri: String,
    visionAnalysisContent: String,
    visionAnalysis: VisionAnalysis,
    navOptions: NavOptions? = null
) {
    this.navigate(
        RecordingScreenRoute(
            imageUri = imageUri,
            visionAnalysisContent = visionAnalysisContent,
            visionAnalysis = visionAnalysis
        ),
        navOptions
    )
}


//   Custom NavType 정의
//  Data Class 안에 Data Class를 전달할때 Bundle 보다는 Json 형태로 전달하여 직렬화 과정에 안정성 확보 위함
val VisionAnalysisType = object : NavType<VisionAnalysis>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): VisionAnalysis? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): VisionAnalysis {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: VisionAnalysis): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: VisionAnalysis) {
        bundle.putString(key, Json.encodeToString(value))
    }
}


fun NavGraphBuilder.momentGraph(
    appState: CmsAppState,
    useCompose: Boolean = true
) {
    val navController = appState.navController

    if (useCompose) {
        // HomeRoute.Moment로 직접 연결
        composable<HomeRoute.Moment> {
            CameraRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateUp = {
                    navController.navigateUp()
                },
                onNavigateToRecording = { imageUri, visionAnalysisContent, visionAnalysis ->
                    navController.navigateToRecording(
                        imageUri = imageUri,
                        visionAnalysisContent = visionAnalysisContent,
                        visionAnalysis = visionAnalysis
                    )
                }
            )
        }

        composable<RecordingScreenRoute>(
            typeMap = mapOf(typeOf<VisionAnalysis>() to VisionAnalysisType)
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<RecordingScreenRoute>()
            RecordingRoute(
                imageUri = route.imageUri,
                visionAnalysisContent = route.visionAnalysisContent,
                visionAnalysis = route.visionAnalysis,
                onBackClick = {
                    navController.popBackStack()
                },
                onCompleted = {
                    navController.popBackStack(HomeRoute.Moment, inclusive = true)
                }
            )
        }
    } else {
        // XML Navigation
        navigation(
            route = MOMENT_GRAPH,
            startDestination = MOMENT_ROUTE
        ) {
            composable(route = MOMENT_ROUTE) {
                CameraRoute(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateUp = {
                        navController.navigateUp()
                    },
                    onNavigateToRecording = { imageUri, visionAnalysisContent, visionAnalysis->
                        navController.navigateToRecording(imageUri, visionAnalysisContent, visionAnalysis)
                    }
                )
            }
        }
    }
}