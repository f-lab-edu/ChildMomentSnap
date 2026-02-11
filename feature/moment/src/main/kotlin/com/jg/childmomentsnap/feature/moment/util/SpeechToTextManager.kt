package com.jg.childmomentsnap.feature.moment.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.jg.childmomentsnap.feature.moment.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale


data class VoiceToTextParserState(
    val spokenText: String = "",
    val isSpeaking: Boolean = false,
    val error: String? = null
)

//  관리 (인텐트 필드 조건, 리스너 구현)
class SpeechToTextManager(private val context: Context) {

    private val _state = MutableStateFlow(VoiceToTextParserState())
    val state = _state.asStateFlow()

    private var speechRecognizer : SpeechRecognizer? = null

    private val recognitionListener = object : RecognitionListener {
        override fun onBeginningOfSpeech() {
        }

        override fun onBufferReceived(p0: ByteArray?) {
        }

        override fun onEndOfSpeech() {
        }

        override fun onError(error: Int) {
            val errorMessage = mapErrorCode(error)

            when (error) {
                SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                    //   해당 에러는 다시 시작
                    startListening()
                }
                else -> {
                    _state.update { current ->
                        current.copy(
                            isSpeaking = false,
                            error = errorMessage,
                        )
                    }
                }
            }
        }

        override fun onEvent(p0: Int, p1: Bundle?) {

        }

        override fun onPartialResults(p0: Bundle?) {

        }

        override fun onReadyForSpeech(p0: Bundle?) {

        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            if (!matches.isNullOrEmpty()) { // 결과가 null 혹은 empty가 아니라면
                val recognizedText = matches[0]
                if (recognizedText.isNotBlank()) { // 결과가 blank가 아니면, 결과 출력
                    _state.update { current ->
                        current.copy(
                            spokenText = recognizedText
                        )
                    }
                }
            }
        }

        override fun onRmsChanged(p0: Float) {

        }
    }


    private fun createSpeechRecognitionIntent() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "듣고 있어요...")
    }


    private fun mapErrorCode(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> context.getString(R.string.feature_moment_stt_error_audio)
            SpeechRecognizer.ERROR_CLIENT -> context.getString(R.string.feature_moment_stt_error_client)
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> context.getString(R.string.feature_moment_stt_error_insufficient_permissions)
            SpeechRecognizer.ERROR_NETWORK -> context.getString(R.string.feature_moment_stt_error_network)
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> context.getString(R.string.feature_moment_stt_error_network_timeout)
            SpeechRecognizer.ERROR_NO_MATCH -> context.getString(R.string.feature_moment_stt_error_no_match)
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> context.getString(R.string.feature_moment_stt_error_recognizer_busy)
            SpeechRecognizer.ERROR_SERVER -> context.getString(R.string.feature_moment_stt_error_server)
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> context.getString(R.string.feature_moment_stt_error_speech_timeout)
            // API 31+ 에러 코드
            10 -> context.getString(R.string.feature_moment_stt_error_too_many_requests)       // ERROR_TOO_MANY_REQUESTS
            11 -> context.getString(R.string.feature_moment_stt_error_server_disconnected)      // ERROR_SERVER_DISCONNECTED
            12 -> context.getString(R.string.feature_moment_stt_error_language_not_supported)   // ERROR_LANGUAGE_NOT_SUPPORTED
            13 -> context.getString(R.string.feature_moment_stt_error_language_unavailable)     // ERROR_LANGUAGE_UNAVAILABLE
            else -> context.getString(R.string.feature_moment_stt_error_unknown, error)
        }
    }

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _state.update { current ->
                current.copy(
                    error = context.getString(R.string.feature_moment_stt_error_not_available)
                )
            }
            return
        }

        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(recognitionListener)
            }
        }

        speechRecognizer?.startListening(createSpeechRecognitionIntent())

        _state.update { current ->
            current.copy(isSpeaking = true, spokenText = "")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {

        }
        _state.update { current ->
            current.copy(isSpeaking = false)
        }
    }

    fun destroy() {
        destroyRecognizer()
    }

    private fun destroyRecognizer() {
        try {
            speechRecognizer?.apply {
                stopListening()
                cancel()
                destroy()
            }
        } catch (e: Exception) {
            // 정리 중 에러 무시
        } finally {
            speechRecognizer = null
        }
    }
}