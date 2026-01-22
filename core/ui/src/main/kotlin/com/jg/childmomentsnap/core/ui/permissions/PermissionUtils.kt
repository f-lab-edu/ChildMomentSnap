package com.jg.childmomentsnap.core.ui.permissions

import android.Manifest
import android.content.Context
import com.jg.childmomentsnap.core.ui.R

object AppPermissions {

    const val CAMERA = Manifest.permission.CAMERA

    const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    
    // Android 13+ (API 33+) 새로운 미디어 권한
    const val READ_MEDIA_IMAGES = Manifest.permission.READ_MEDIA_IMAGES
    const val READ_MEDIA_VIDEO = Manifest.permission.READ_MEDIA_VIDEO

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

        val VOICE_PERMISSIONS = listOf(
            RECORD_AUDIO,
            READ_MEDIA_VIDEO
        )
        
        // Android 12 이하를 위한 레거시 비디오 권한
        val VOICE_PERMISSIONS_LEGACY = listOf(
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
         * Android 버전에 따라 적절한 음성 녹음 권한을 반환
         */
        fun getVoicePermissions(): List<String> {
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                VOICE_PERMISSIONS // Android 13+ (API 33+)
            } else {
                VOICE_PERMISSIONS_LEGACY // Android 12 이하
            }
        }

        /**
         * 위치 권한을 가져온다
         */
        fun getLocationPermission(): List<String> {
            return LOCATION_PERMISSIONS
        }
    }
}

object PermissionUtils {

    fun getPermissionName(context: Context, permission: String): String {
        return when (permission) {
            AppPermissions.CAMERA -> context.getString(R.string.permission_camera)
            AppPermissions.READ_EXTERNAL_STORAGE -> context.getString(R.string.permission_read_external_storage)
            AppPermissions.WRITE_EXTERNAL_STORAGE -> context.getString(R.string.permission_write_external_storage)
            AppPermissions.READ_MEDIA_IMAGES -> context.getString(R.string.permission_read_media_images)
            AppPermissions.READ_MEDIA_VIDEO -> context.getString(R.string.permission_read_media_video)
            AppPermissions.RECORD_AUDIO -> context.getString(R.string.permission_record_audio)
            AppPermissions.ACCESS_FINE_LOCATION -> context.getString(R.string.permission_access_fine_location)
            AppPermissions.ACCESS_COARSE_LOCATION -> context.getString(R.string.permission_access_coarse_location)
            AppPermissions.POST_NOTIFICATIONS -> context.getString(R.string.permission_post_notifications)
            else -> permission.substringAfterLast(".")
        }
    }

    fun getPermissionDescription(context: Context, permission: String): String {
        return when (permission) {
            AppPermissions.CAMERA -> context.getString(R.string.permission_camera_description)
            AppPermissions.READ_EXTERNAL_STORAGE -> context.getString(R.string.permission_read_external_storage_description)
            AppPermissions.WRITE_EXTERNAL_STORAGE -> context.getString(R.string.permission_write_external_storage_description)
            AppPermissions.READ_MEDIA_IMAGES -> context.getString(R.string.permission_read_media_images_description)
            AppPermissions.READ_MEDIA_VIDEO -> context.getString(R.string.permission_read_media_video_description)
            AppPermissions.RECORD_AUDIO -> context.getString(R.string.permission_record_audio_description)
            AppPermissions.ACCESS_FINE_LOCATION -> context.getString(R.string.permission_access_fine_location_description)
            AppPermissions.ACCESS_COARSE_LOCATION -> context.getString(R.string.permission_access_coarse_location_description)
            AppPermissions.POST_NOTIFICATIONS -> context.getString(R.string.permission_post_notifications_description)
            else -> context.getString(R.string.permission_default_description)
        }
    }
}