package com.jg.childmomentsnap.core.ui.permissions

import android.Manifest

object AppPermissions {

    const val CAMERA = Manifest.permission.CAMERA

    const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    
    // Android 13+ (API 33+) 새로운 미디어 권한
    const val READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES"
    const val READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO"

    const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO

    const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

    const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

    const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS

    object Groups {

        val PHOTO_PERMISSIONS = listOf(
            CAMERA,
            READ_MEDIA_IMAGES
        )
        
        // Android 12 이하를 위한 레거시 권한
        val PHOTO_PERMISSIONS_LEGACY = listOf(
            CAMERA,
            READ_EXTERNAL_STORAGE
        )

        val VIDEO_PERMISSIONS = listOf(
            CAMERA,
            RECORD_AUDIO,
            READ_MEDIA_VIDEO
        )
        
        // Android 12 이하를 위한 레거시 비디오 권한
        val VIDEO_PERMISSIONS_LEGACY = listOf(
            CAMERA,
            RECORD_AUDIO,
            READ_EXTERNAL_STORAGE
        )

        val LOCATION_PERMISSIONS = listOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )
        
        /**
         * Android 버전에 따라 적절한 사진 권한을 반환
         */
        fun getPhotoPermissions(): List<String> {
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                PHOTO_PERMISSIONS // Android 13+ (API 33+)
            } else {
                PHOTO_PERMISSIONS_LEGACY // Android 12 이하
            }
        }
        
        /**
         * Android 버전에 따라 적절한 비디오 권한을 반환
         */
        fun getVideoPermissions(): List<String> {
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                VIDEO_PERMISSIONS // Android 13+ (API 33+)
            } else {
                VIDEO_PERMISSIONS_LEGACY // Android 12 이하
            }
        }
    }
}

object PermissionUtils {

    fun getPermissionName(permission: String): String {
        return when (permission) {
            AppPermissions.CAMERA -> "카메라"
            AppPermissions.READ_EXTERNAL_STORAGE -> "저장소 읽기"
            AppPermissions.WRITE_EXTERNAL_STORAGE -> "저장소 쓰기"
            AppPermissions.READ_MEDIA_IMAGES -> "사진 접근"
            AppPermissions.READ_MEDIA_VIDEO -> "동영상 접근"
            AppPermissions.RECORD_AUDIO -> "마이크"
            AppPermissions.ACCESS_FINE_LOCATION -> "정확한 위치"
            AppPermissions.ACCESS_COARSE_LOCATION -> "대략적인 위치"
            AppPermissions.POST_NOTIFICATIONS -> "알림"
            else -> permission.substringAfterLast(".")
        }
    }

    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            AppPermissions.CAMERA -> "사진을 촬영하기 위해 카메라 권한이 필요합니다."
            AppPermissions.READ_EXTERNAL_STORAGE -> "갤러리에서 사진을 선택하기 위해 저장소 접근 권한이 필요합니다."
            AppPermissions.WRITE_EXTERNAL_STORAGE -> "사진을 저장하기 위해 저장소 쓰기 권한이 필요합니다."
            AppPermissions.READ_MEDIA_IMAGES -> "갤러리에서 사진을 선택하기 위해 사진 접근 권한이 필요합니다."
            AppPermissions.READ_MEDIA_VIDEO -> "갤러리에서 동영상을 선택하기 위해 동영상 접근 권한이 필요합니다."
            AppPermissions.RECORD_AUDIO -> "음성을 녹음하기 위해 마이크 권한이 필요합니다."
            AppPermissions.ACCESS_FINE_LOCATION -> "정확한 위치 정보를 사용하기 위해 위치 권한이 필요합니다."
            AppPermissions.ACCESS_COARSE_LOCATION -> "대략적인 위치 정보를 사용하기 위해 위치 권한이 필요합니다."
            AppPermissions.POST_NOTIFICATIONS -> "알림을 보내기 위해 알림 권한이 필요합니다."
            else -> "앱을 정상적으로 사용하기 위해 권한이 필요합니다."
        }
    }
}