package com.jg.childmomentsnap.feature.moment.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class RecordingData(
    val durationMs: Long = 0L,
    val maxAmplitude: Int = 0
)

class VoiceRecorder @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var mediaRecorder: MediaRecorder? = null
    private var recordingJob: Job? = null

    private val _recordingData = MutableStateFlow(RecordingData())
    val recordingData: StateFlow<RecordingData> = _recordingData.asStateFlow()

    private var currentDuration = 0L

    fun startRecording(filePath: String, scope: CoroutineScope) {
        stopRecording()

        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }

        try {
            mediaRecorder = createMediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(filePath)
                prepare()
                start()
            }
            
            currentDuration = 0L
            startTimer(scope)
            
        } catch (e: Exception) {
            stopRecording()
            throw e
        }
    }

    private fun createMediaRecorder(context: Context): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun pauseRecording() {
        try {
            mediaRecorder?.pause()
            recordingJob?.cancel()
            recordingJob = null
        } catch (e: Exception) {
            // Handle pause error if needed
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun resumeRecording(scope: CoroutineScope) {
        try {
            mediaRecorder?.resume()
            startTimer(scope)
        } catch (e: Exception) {
            // Handle resume error if needed
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder?.let {
                it.stop()
                it.release()
            }
        } catch (e: Exception) {
            // Ignore if already stopped
        } finally {
            mediaRecorder = null
            recordingJob?.cancel()
            recordingJob = null
            currentDuration = 0L
            _recordingData.update { RecordingData() }
        }
    }

    private fun startTimer(scope: CoroutineScope) {
        recordingJob?.cancel()
        recordingJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(100)
                currentDuration += 100
                val amplitude = try {
                    mediaRecorder?.maxAmplitude ?: 0
                } catch (e: Exception) {
                    0
                }
                _recordingData.update {
                    RecordingData(
                        durationMs = currentDuration,
                        maxAmplitude = amplitude
                    )
                }
            }
        }
    }
}
