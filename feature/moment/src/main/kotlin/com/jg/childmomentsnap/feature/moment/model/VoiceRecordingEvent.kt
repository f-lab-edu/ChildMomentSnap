package com.jg.childmomentsnap.feature.moment.model

/**
 * 음성 녹음 관련 이벤트들
 * Screen -> ViewModel 간의 안전한 이벤트 전달을 위한 sealed class
 */
sealed interface VoiceRecordingEvent {
    /**
     * BottomSheet 표시 요청
     */
    data object ShowBottomSheet : VoiceRecordingEvent
    
    /**
     * BottomSheet 닫기 요청
     */
    data object DismissBottomSheet : VoiceRecordingEvent
    
    /**
     * 녹음 완료 이벤트
     * @param voiceFilePath 녹음된 파일 경로 (null이면 녹음 실패)
     */
    data class RecordingCompleted(val voiceFilePath: String?) : VoiceRecordingEvent
    
    /**
     * 녹음 취소 이벤트
     */
    data object RecordingCancelled : VoiceRecordingEvent
}

/**
 * ViewModel -> Screen 네비게이션 이벤트들
 * ViewModel에서 Screen으로 안전하게 이벤트를 전달하기 위한 sealed class
 */
sealed interface VoiceRecordingNavigationEvent {
    /**
     * 녹음 완료 후 CameraViewModel에 결과 전달 필요
     */
    data class NotifyRecordingCompleted(val voiceFilePath: String?) : VoiceRecordingNavigationEvent
    
    /**
     * 에러 발생
     */
    data class ShowError(val message: String) : VoiceRecordingNavigationEvent
}