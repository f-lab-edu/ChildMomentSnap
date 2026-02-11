package com.jg.childmomentsnap.feature.moment.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

// 생성과 제거를 관리
@Composable
internal fun rememberSpeechToTextManager(
): SpeechToTextManager {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val speechToTextManager = remember {
        SpeechToTextManager(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    speechToTextManager.stopListening()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            speechToTextManager.destroy()
        }
    }

    return speechToTextManager
}