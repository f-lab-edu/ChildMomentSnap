package com.jg.childmomentsnap.feature.photo

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


/**
 * Camera route
 *  1. Camera Route 에서 Camera & 저장소 권한 접근
 *  2.
 *
 */
@Composable
internal fun CameraRoute(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel()
) {

    Column() {
        Text(
            text = "Camera Route"
        )
    }

}

@Composable
private fun CameraScreen(

) {

}