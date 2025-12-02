package com.jg.childmomentsnap.core.ui.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

fun Context.hasPermission(permission: String): Boolean {
    // ContextCompat.checkSelfPermission을 사용하여 권한 상태를 확인
    // PERMISSION_GRANTED와 비교하여 boolean 값으로 반환
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}


fun Context.hasAllPermissions(permissions: List<String>): Boolean {
    // permissions 리스트의 모든 권한에 대해 hasPermission()을 호출하고
    // 모든 권한이 허용되었는지 확인
    return permissions.all { hasPermission(it) }
}