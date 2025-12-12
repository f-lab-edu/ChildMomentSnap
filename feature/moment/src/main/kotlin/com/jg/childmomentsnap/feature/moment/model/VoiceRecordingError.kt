package com.jg.childmomentsnap.feature.moment.model

/**
 * 음성 녹음 관련 에러 타입
 * String Resource ID와 매핑하여 사용
 */
enum class VoiceRecordingError {
    /** 녹음 파일 경로가 설정되지 않음 */
    RECORDING_FILE_PATH_NOT_SET,
    
    /** 녹음 시작 중 오류 */
    RECORDING_START_FAILED,
    
    /** 녹음 일시정지 중 오류 */
    RECORDING_PAUSE_FAILED,
    
    /** 녹음 재개 중 오류 */
    RECORDING_RESUME_FAILED,
    
    /** 녹음 정지 중 오류 */
    RECORDING_STOP_FAILED,
    
    /** 재생할 녹음 파일이 없음 */
    NO_RECORDING_FILE_TO_PLAY,
    
    /** 녹음 파일을 찾을 수 없음 */
    RECORDING_FILE_NOT_FOUND,
    
    /** 재생 시작 중 오류 */
    PLAYBACK_START_FAILED,
    
    /** 재생 중 오류 */
    PLAYBACK_ERROR,
    
    /** 재생 정지 중 오류 */
    PLAYBACK_STOP_FAILED,
    
    /** 리셋 중 오류 */
    RESET_FAILED
}