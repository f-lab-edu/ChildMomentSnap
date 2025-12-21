package com.jg.childmomentsnap.feature.moment.model

import com.jg.childmomentsnap.core.model.MomentData

/**
 * 음성 녹음 관련 UI 이벤트
 * ViewModel에서 Screen으로 일회성 이벤트를 전달하기 위한 sealed class
 */
sealed interface VoiceRecordingUiEffect {
    /**
     * 에러 메시지 Toast 표시
     * @param errorType 에러 타입 (String Resource와 매핑)
     */
    data class ShowErrorToast(val errorType: VoiceRecordingError) : VoiceRecordingUiEffect
    
    /**
     * 녹음 완료 알림
     * @param voiceFilePath 녹음된 파일 경로 (null이면 녹음 실패)
     */
    data class NotifyRecordingCompleted(val voiceFilePath: String?) : VoiceRecordingUiEffect

    data class MomentAnalysisCompleted(val momentData: MomentData) : VoiceRecordingUiEffect
}