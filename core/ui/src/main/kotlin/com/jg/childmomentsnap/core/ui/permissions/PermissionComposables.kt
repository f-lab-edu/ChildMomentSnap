package com.jg.childmomentsnap.core.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * Android 권한 처리를 위한 Compose 함수들
 * 
 * 이 파일은 ChildMomentSnap 앱에서 사용되는 권한 관련 Composable 함수들을 제공합니다.
 * 카메라, 갤러리, 마이크 등의 권한을 선언적이고 재사용 가능한 방식으로 처리할 수 있습니다.
 * 
 * 주요 기능:
 * - 단일/복수 권한 요청 런처 제공
 * - 자동 권한 처리 및 조건부 UI 렌더링
 * - 권한 상태에 따른 선언적 컴포넌트 구성
 * 
 * 사용 예시:
 * ```kotlin
 * // 단일 권한 처리
 * val cameraLauncher = rememberPermissionLauncher(
 *     permission = AppPermissions.CAMERA,
 *     onGranted = { startCamera() },
 *     onDenied = { showPermissionDeniedMessage() }
 * )
 * 
 * // 자동 권한 처리
 * PermissionHandler(
 *     permission = AppPermissions.CAMERA,
 *     onPermissionGranted = { CameraPreview() },
 *     onPermissionDenied = { PermissionRequiredMessage() }
 * )
 * ```
 * 
 * @author Jg
 * @since 1.0.0
 */

/**
 * 단일 권한 요청을 위한 런처를 제공하는 Composable 함수
 * 
 * 이 함수는 remember를 사용하여 권한 요청 런처를 생성하고 반환합니다.
 * 권한이 이미 허용되어 있으면 즉시 onGranted 콜백을 실행하고,
 * 그렇지 않으면 시스템 권한 요청 다이얼로그를 표시합니다.
 * 
 * @param permission 요청할 권한 (예: Manifest.permission.CAMERA)
 * @param onGranted 권한이 허용되었을 때 실행할 콜백 함수
 * @param onDenied 권한이 거부되었을 때 실행할 콜백 함수 (기본값: 빈 함수)
 * 
 * @return 권한 요청을 시작하는 함수. 버튼 클릭 등의 이벤트에서 호출할 수 있습니다.
 * 
 * 사용 예시:
 * ```kotlin
 * val cameraLauncher = rememberPermissionLauncher(
 *     permission = AppPermissions.CAMERA,
 *     onGranted = { 
 *         // 카메라 권한이 허용됨 - 카메라 실행
 *         startCameraCapture()
 *     },
 *     onDenied = { 
 *         // 카메라 권한이 거부됨 - 사용자에게 알림
 *         showToast("카메라 권한이 필요합니다")
 *     }
 * )
 * 
 * Button(onClick = cameraLauncher) {
 *     Text("사진 촬영")
 * }
 * ```
 * 
 * 주의사항:
 * - 이 함수는 Composition 범위에서만 호출해야 합니다
 * - 권한 요청은 사용자 액션(버튼 클릭 등)에 의해 트리거되어야 합니다
 * - 이미 허용된 권한에 대해서는 시스템 다이얼로그 없이 즉시 onGranted가 호출됩니다
 */
@Composable
fun rememberPermissionLauncher(
    permission: String,
    onGranted: () -> Unit,
    onDenied: () -> Unit = {}
): () -> Unit {
    val context = LocalContext.current
    
    // ActivityResultContracts.RequestPermission을 사용하여 권한 요청 런처를 생성
    // remember를 통해 recomposition 간에 런처 인스턴스를 보존
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // 권한 요청 결과에 따라 적절한 콜백을 실행
        if (isGranted) {
            onGranted()
        } else {
            onDenied()
        }
    }
    
    // 권한 요청을 실행하는 함수를 반환
    return {
        if (context.hasPermission(permission)) {
            // 이미 권한이 허용되어 있다면 즉시 onGranted 실행
            onGranted()
        } else {
            // 권한이 없다면 시스템 권한 요청 다이얼로그 표시
            permissionLauncher.launch(permission)
        }
    }
}

/**
 * 복수 권한 요청을 위한 런처를 제공하는 Composable 함수
 * 
 * 여러 개의 권한을 동시에 요청해야 하는 경우에 사용합니다.
 * 예를 들어, 동영상 촬영 시 카메라와 마이크 권한을 모두 필요로 하는 경우에 활용할 수 있습니다.
 * 
 * @param permissions 요청할 권한들의 리스트 (예: [Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO])
 * @param onAllGranted 모든 권한이 허용되었을 때 실행할 콜백 함수
 * @param onSomeRejected 일부 권한이 거부되었을 때 실행할 콜백 함수. 거부된 권한 목록을 매개변수로 받습니다.
 * 
 * @return 권한 요청을 시작하는 함수
 * 
 * 사용 예시:
 * ```kotlin
 * val videoRecordingLauncher = rememberMultiplePermissionsLauncher(
 *     permissions = AppPermissions.Groups.VIDEO_PERMISSIONS,
 *     onAllGranted = {
 *         // 모든 권한(카메라, 마이크)이 허용됨 - 동영상 촬영 시작
 *         startVideoRecording()
 *     },
 *     onSomeRejected = { rejectedPermissions ->
 *         // 일부 권한이 거부됨 - 사용자에게 필요한 권한 안내
 *         val missingPermissions = rejectedPermissions.joinToString(", ") { 
 *             PermissionUtils.getPermissionName(it) 
 *         }
 *         showDialog("다음 권한이 필요합니다: $missingPermissions")
 *     }
 * )
 * 
 * Button(onClick = videoRecordingLauncher) {
 *     Text("동영상 촬영")
 * }
 * ```
 * 
 * 동작 방식:
 * - 모든 권한이 이미 허용되어 있으면 즉시 onAllGranted 실행
 * - 일부 권한이 없으면 시스템 권한 요청 다이얼로그 표시
 * - 사용자가 권한을 허용/거부한 후, 결과에 따라 적절한 콜백 실행
 */
@Composable
fun rememberMultiplePermissionsLauncher(
    permissions: List<String>,
    onAllGranted: () -> Unit,
    onSomeRejected: (List<String>) -> Unit = {}
): () -> Unit {
    // 현재 컨텍스트
    val context = LocalContext.current
    
    // ActivityResultContracts.RequestMultiplePermissions을 사용하여 복수 권한 요청 런처를 생성
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        // 권한 요청 결과를 분석하여 거부된 권한들을 필터
        // permissionsMap은 Map<String, Boolean> 형태로 권한별 허용/거부 상태를 포함
        val rejectedPermissions = permissionsMap.filterValues { !it }.keys.toList()
        
        if (rejectedPermissions.isEmpty()) {
            // 모든 권한이 허용되었음
            onAllGranted()
        } else {
            // 일부 권한이 거부되었음 - 거부된 권한 목록과 함께 콜백 실행
            onSomeRejected(rejectedPermissions)
        }
    }
    
    // 권한 요청을 실행하는 함수를 반환
    return {
        if (context.hasAllPermissions(permissions)) {
            // 이미 모든 권한이 허용되어 있다면 즉시 onAllGranted 실행
            onAllGranted()
        } else {
            // 일부 권한이 없다면 시스템 권한 요청 다이얼로그 표시
            // List를 Array로 변환하여 launch에 전달
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }
}

/**
 * 권한 상태에 따라 자동으로 다른 UI를 렌더링하는 Composable 함수
 * 
 * 이 함수는 권한이 허용되었는지 확인하고, 권한 상태에 따라 적절한 Composable을 렌더링합니다.
 * 선언적 UI 패턴을 사용하여 권한 관련 로직을 캡슐화하고, 조건부 렌더링을 간소화합니다.
 * 
 * @param permission 확인할 권한 (예: Manifest.permission.CAMERA)
 * @param onPermissionGranted 권한이 허용되었을 때 렌더링할 Composable
 * @param onPermissionDenied 권한이 거부되었을 때 렌더링할 Composable (기본값: 빈 Composable)
 * @param requestImmediately true일 경우, 컴포지션 시점에 자동으로 권한을 요청합니다 (기본값: false)
 * 
 * 사용 예시:
 * ```kotlin
 * // 기본 사용법 - 수동 권한 요청
 * PermissionHandler(
 *     permission = AppPermissions.CAMERA,
 *     onPermissionGranted = {
 *         // 권한이 있을 때만 카메라 UI 표시
 *         CameraPreviewScreen()
 *     },
 *     onPermissionDenied = {
 *         // 권한이 없을 때 권한 요청 UI 표시
 *         PermissionRequestCard(
 *             title = "카메라 권한 필요",
 *             description = "사진을 촬영하려면 카메라 권한이 필요합니다",
 *             onRequestClick = { /* 권한 요청 로직 */ }
 *         )
 *     }
 * )
 * 
 * // 자동 권한 요청 - 화면 진입 시 즉시 권한 요청
 * PermissionHandler(
 *     permission = AppPermissions.CAMERA,
 *     requestImmediately = true,
 *     onPermissionGranted = { CameraScreen() },
 *     onPermissionDenied = { PermissionDeniedScreen() }
 * )
 * ```
 * 
 * 동작 방식:
 * 1. 컴포지션 시점에 현재 권한 상태를 확인
 * 2. requestImmediately가 true이고 권한이 없으면 자동으로 권한 요청
 * 3. 권한 상태에 따라 적절한 Composable을 렌더링
 * 4. 권한 상태가 변경되면 자동으로 UI 업데이트
 *
 */
@Composable
fun PermissionHandler(
    permission: String,
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable () -> Unit = {},
    requestImmediately: Boolean = false
) {
    // 현재 컨텍스트
    val context = LocalContext.current
    // 권한 상태를 remember로 관리하여 상태 변경 시 recomposition 트리거
    var permissionState by remember { mutableStateOf(context.hasPermission(permission)) }
    
    // 권한 요청 런처를 생성
    // 권한 상태가 변경되면 permissionState를 업데이트하여 UI를 다시 렌더링
    val launcher = rememberPermissionLauncher(
        permission = permission,
        onGranted = { permissionState = true },
        onDenied = { permissionState = false }
    )
    
    // requestImmediately가 true이고 권한이 없으면 자동으로 권한 요청
    // LaunchedEffect를 사용하여 컴포지션 시점에 한 번만 실행
    if (requestImmediately && !permissionState) {
        LaunchedEffect(permission) {
            launcher()
        }
    }
    
    // 권한 상태에 따라 적절한 Composable을 렌더링
    if (permissionState) {
        // 권한이 허용되었을 때의 UI
        onPermissionGranted()
    } else {
        // 권한이 거부되었을 때의 UI
        onPermissionDenied()
    }
}

/**
 * 복수 권한 상태에 따라 자동으로 다른 UI를 렌더링하는 Composable 함수
 * 
 * 여러 권한이 필요한 기능에 대해 권한 상태를 확인하고, 모든 권한이 허용되었는지에 따라 
 * 적절한 Composable을 렌더링합니다. 동영상 촬영, 위치 기반 서비스 등 복수 권한이 필요한 
 * 기능에서 활용할 수 있습니다.
 * 
 * @param permissions 확인할 권한들의 리스트 (예: [Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO])
 * @param onAllPermissionsGranted 모든 권한이 허용되었을 때 렌더링할 Composable
 * @param onSomePermissionsDenied 일부 권한이 거부되었을 때 렌더링할 Composable. 거부된 권한 목록을 매개변수로 받습니다.
 * @param requestImmediately true일 경우, 컴포지션 시점에 자동으로 권한을 요청합니다 (기본값: false)
 * 
 * 사용 예시:
 * ```kotlin
 * // 동영상 촬영 기능 - 카메라와 마이크 권한 모두 필요
 * MultiplePermissionsHandler(
 *     permissions = AppPermissions.Groups.VIDEO_PERMISSIONS,
 *     onAllPermissionsGranted = {
 *         // 모든 권한이 허용됨 - 동영상 촬영 UI 표시
 *         VideoRecordingScreen()
 *     },
 *     onSomePermissionsDenied = { deniedPermissions ->
 *         // 일부 권한이 거부됨 - 필요한 권한들에 대한 안내 UI 표시
 *         MultiplePermissionsRequestCard(
 *             deniedPermissions = deniedPermissions,
 *             onRequestAllClick = { /* 모든 권한 재요청 */ },
 *             requiredFeature = "동영상 촬영"
 *         )
 *     }
 * )
 * 
 * // 자동 권한 요청 버전
 * MultiplePermissionsHandler(
 *     permissions = listOf(AppPermissions.ACCESS_FINE_LOCATION, AppPermissions.CAMERA),
 *     requestImmediately = true,
 *     onAllPermissionsGranted = { LocationBasedCameraScreen() },
 *     onSomePermissionsDenied = { deniedPermissions ->
 *         PartialPermissionDeniedScreen(
 *             missingPermissions = deniedPermissions.map { 
 *                 PermissionUtils.getPermissionName(it) 
 *             }
 *         )
 *     }
 * )
 * ```
 * 
 * 동작 방식:
 * 1. 컴포지션 시점에 모든 권한의 현재 상태를 확인
 * 2. 거부된 권한들의 목록을 상태로 관리
 * 3. requestImmediately가 true이고 거부된 권한이 있으면 자동으로 권한 요청
 * 4. 모든 권한이 허용되었는지에 따라 적절한 Composable을 렌더링
 * 5. 권한 상태가 변경되면 자동으로 UI 업데이트
 */
@Composable
fun MultiplePermissionsHandler(
    permissions: List<String>,
    onAllPermissionsGranted: @Composable () -> Unit,
    onSomePermissionsDenied: @Composable (List<String>) -> Unit = {},
    requestImmediately: Boolean = false
) {

    val context = LocalContext.current
    
    // 거부된 권한들의 목록을 상태로 관리
    // 초기값은 현재 허용되지 않은 권한들의 목록
    var deniedPermissions by remember { 
        mutableStateOf(permissions.filter { !context.hasPermission(it) })
    }
    
    // 복수 권한 요청 런처를 생성
    // 권한 상태가 변경되면 deniedPermissions를 업데이트하여 UI를 다시 렌더링
    val launcher = rememberMultiplePermissionsLauncher(
        permissions = permissions,
        onAllGranted = { 
            // 모든 권한이 허용됨 - 거부된 권한 목록을 비움
            deniedPermissions = emptyList() 
        },
        onSomeRejected = { rejected -> 
            // 일부 권한이 거부됨 - 거부된 권한 목록을 업데이트
            deniedPermissions = rejected 
        }
    )
    
    // requestImmediately가 true이고 거부된 권한이 있으면 자동으로 권한 요청
    // LaunchedEffect를 사용하여 컴포지션 시점에 한 번만 실행
    if (requestImmediately && deniedPermissions.isNotEmpty()) {
        LaunchedEffect(permissions) {
            launcher()
        }
    }
    
    // 권한 상태에 따라 적절한 Composable을 렌더링
    if (deniedPermissions.isEmpty()) {
        // 모든 권한이 허용되었을 때의 UI
        onAllPermissionsGranted()
    } else {
        // 일부 권한이 거부되었을 때의 UI
        // 거부된 권한 목록을 매개변수로 전달하여 구체적인 안내를 가능
        onSomePermissionsDenied(deniedPermissions)
    }
}