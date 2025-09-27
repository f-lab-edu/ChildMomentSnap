package com.jg.childmomentsnap.core.ui.permissions

import android.Manifest

object AppPermissions {

    const val CAMERA = Manifest.permission.CAMERA

    const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

    const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO

    const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

    const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

    const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS

    object Groups {

        val PHOTO_PERMISSIONS = listOf(
            CAMERA,
            READ_EXTERNAL_STORAGE
        )

        val VIDEO_PERMISSIONS = listOf(
            CAMERA,
            RECORD_AUDIO,
            READ_EXTERNAL_STORAGE
        )

        val LOCATION_PERMISSIONS = listOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )
    }
}

object PermissionUtils {

    fun getPermissionName(permission: String): String {
        return when (permission) {
            AppPermissions.CAMERA -> "카메라"
            AppPermissions.READ_EXTERNAL_STORAGE -> "저장소 읽기"
            AppPermissions.WRITE_EXTERNAL_STORAGE -> "저장소 쓰기"
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
            AppPermissions.RECORD_AUDIO -> "음성을 녹음하기 위해 마이크 권한이 필요합니다."
            AppPermissions.ACCESS_FINE_LOCATION -> "정확한 위치 정보를 사용하기 위해 위치 권한이 필요합니다."
            AppPermissions.ACCESS_COARSE_LOCATION -> "대략적인 위치 정보를 사용하기 위해 위치 권한이 필요합니다."
            AppPermissions.POST_NOTIFICATIONS -> "알림을 보내기 위해 알림 권한이 필요합니다."
            else -> "앱을 정상적으로 사용하기 위해 권한이 필요합니다."
        }
    }
}