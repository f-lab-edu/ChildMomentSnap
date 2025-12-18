package com.jg.childmomentsnap.feature.moment.util

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class VoicePlayer @Inject constructor() {

    private var mediaPlayer: MediaPlayer? = null
    private var playbackJob: Job? = null

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition: StateFlow<Long> = _playbackPosition.asStateFlow()

    fun play(
        filePath: String,
        scope: CoroutineScope,
        onCompletion: () -> Unit,
        onError: () -> Unit
    ) {
        stopPlayback()

        val file = File(filePath)
        if (!file.exists()) {
            onError()
            return
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                    startTimer(scope)
                }
                setOnCompletionListener {
                    stopPlayback()
                    onCompletion()
                }
                setOnErrorListener { _, _, _ ->
                    stopPlayback()
                    onError()
                    true
                }
            }
        } catch (e: Exception) {
            stopPlayback()
            onError()
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) {
            // Ignore
        } finally {
            mediaPlayer = null
            playbackJob?.cancel()
            playbackJob = null
            _playbackPosition.value = 0L
        }
    }

    private fun startTimer(scope: CoroutineScope) {
        playbackJob?.cancel()
        playbackJob = scope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(100)
                try {
                    mediaPlayer?.let { mp ->
                        if (mp.isPlaying) {
                            _playbackPosition.value = mp.currentPosition.toLong()
                        }
                    }
                } catch (e: Exception) {
                    // Ignore if illegal state or other errors during polling
                }
            }
        }
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }
}
