package com.jg.childmomentsnap.core.ui.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * Android 권한 처리를 위한 Context와 ComponentActivity의 확장 함수들
 * 
 * 이 파일은 ChildMomentSnap 앱에서 사용되는 권한 관련 확장 함수들을 제공합니다.
 * Context와 ComponentActivity에 권한 확인 및 요청 기능을 추가하여,
 * 앱 전반에서 일관된 방식으로 권한을 처리할 수 있습니다.
 * 
 * 주요 기능:
 * - 단일/복수 권한 상태 확인
 * - ComponentActivity에서 직접적인 권한 요청
 * - ActivityResult API를 활용한 현대적인 권한 처리
 * 
 * 사용 예시:
 * ```kotlin
 * // 권한 상태 확인
 * if (context.hasPermission(AppPermissions.CAMERA)) {
 *     // 카메라 사용 가능
 * }
 * 
 * // Activity에서 권한 요청
 * requestPermission(
 *     permission = AppPermissions.CAMERA,
 *     onGranted = { startCamera() },
 *     onDenied = { showPermissionExplanation() }
 * )
 * ```
 * 
 * 참고: Compose UI에서는 PermissionComposables.kt의 함수들을 사용하는 것을 권장합니다.
 * 
 * @author Jg
 * @since 1.0.0
 */

/**
 * 단일 권한이 허용되었는지 확인하는 Context 확장 함수
 * 
 * Android의 런타임 권한 시스템을 사용하여 특정 권한의 허용 상태를 확인합니다.
 * 이 함수는 어떤 Context 인스턴스에서도 호출할 수 있으며, 
 * ContextCompat.checkSelfPermission을 래핑하여 사용법을 간소화합니다.
 * 
 * @param permission 확인할 권한 문자열 (예: Manifest.permission.CAMERA)
 * @return 권한이 허용되었으면 true, 그렇지 않으면 false
 * 
 * 사용 예시:
 * ```kotlin
 * // 카메라 권한 확인
 * if (context.hasPermission(AppPermissions.CAMERA)) {
 *     // 카메라 기능 사용 가능
 *     openCamera()
 * } else {
 *     // 권한 요청 필요
 *     showPermissionRationale()
 * }
 * 
 * // Fragment에서 사용
 * if (requireContext().hasPermission(AppPermissions.READ_EXTERNAL_STORAGE)) {
 *     loadImagesFromGallery()
 * }
 * 
 * // Activity에서 사용
 * if (hasPermission(AppPermissions.RECORD_AUDIO)) {
 *     startAudioRecording()
 * }
 * ```
 * 
 * 참고:
 * - 이 함수는 현재 권한 상태만 확인하며, 권한 요청은 하지 않습니다
 * - API 23 (Android 6.0) 미만에서는 항상 true를 반환합니다 (설치 시점 권한)
 * - 권한 요청이 필요한 경우 requestPermission() 함수를 사용하세요
 */
fun Context.hasPermission(permission: String): Boolean {
    // ContextCompat.checkSelfPermission을 사용하여 권한 상태를 확인
    // PERMISSION_GRANTED와 비교하여 boolean 값으로 반환
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

/**
 * 복수 권한이 모두 허용되었는지 확인하는 Context 확장 함수
 * 
 * 여러 권한이 필요한 기능에서 모든 권한이 허용되었는지 한 번에 확인합니다.
 * 내부적으로 hasPermission() 함수를 사용하여 각 권한을 개별적으로 확인하고,
 * 모든 권한이 허용된 경우에만 true를 반환합니다.
 * 
 * @param permissions 확인할 권한들의 리스트
 * @return 모든 권한이 허용되었으면 true, 하나라도 거부되었으면 false
 * 
 * 사용 예시:
 * ```kotlin
 * // 동영상 촬영에 필요한 모든 권한 확인
 * val videoPermissions = AppPermissions.Groups.VIDEO_PERMISSIONS
 * if (context.hasAllPermissions(videoPermissions)) {
 *     // 카메라와 마이크 권한이 모두 허용됨
 *     startVideoRecording()
 * } else {
 *     // 일부 권한이 없음 - 권한 요청 필요
 *     requestVideoPermissions()
 * }
 * 
 * // 위치 기반 서비스에 필요한 권한 확인
 * val locationPermissions = listOf(
 *     AppPermissions.ACCESS_FINE_LOCATION,
 *     AppPermissions.ACCESS_COARSE_LOCATION
 * )
 * if (context.hasAllPermissions(locationPermissions)) {
 *     enableLocationFeatures()
 * }
 * 
 * // 사진 관련 기능에 필요한 권한 확인
 * val photoPermissions = AppPermissions.Groups.PHOTO_PERMISSIONS
 * if (hasAllPermissions(photoPermissions)) {
 *     openPhotoEditor()
 * }
 * ```
 * 
 * 동작 방식:
 * - 리스트의 각 권한에 대해 hasPermission()을 호출합니다
 * - Kotlin의 all() 함수를 사용하여 모든 권한이 허용되었는지 확인합니다
 * - 하나라도 거부된 권한이 있으면 즉시 false를 반환합니다 (단락 평가)
 * 
 * 참고:
 * - 빈 리스트를 전달하면 true를 반환합니다
 * - 일부 권한만 확인하려면 개별적으로 hasPermission()을 사용하세요
 * - 거부된 권한이 무엇인지 알고 싶다면 filter를 사용하여 별도로 확인하세요
 */
fun Context.hasAllPermissions(permissions: List<String>): Boolean {
    // permissions 리스트의 모든 권한에 대해 hasPermission()을 호출하고
    // 모든 권한이 허용되었는지 확인
    return permissions.all { hasPermission(it) }
}