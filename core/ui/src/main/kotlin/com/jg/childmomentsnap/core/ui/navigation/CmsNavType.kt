package com.jg.childmomentsnap.core.ui.navigation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable as KotlinSerializable
import java.io.Serializable

/**
 *  네비게이션 아규먼트 키 (레거시 지원)
 */
const val NavArgKey = "cmsArgName"


/**
 *  네비게이션 커리 파라미터가 명시된 목적 링크 생성 (주소 명세)
 */
fun String.asNavParam() = this.plus(other = "?$NavArgKey={${NavArgKey}}")

/**
 *  네비게이션 쿼리 아규먼트가 주입된 목적 링크 생성 (실제 Query Parameter 추가된 주소 명세)
 */
inline fun <reified T> String.asNavArg(arg: T): String {
    val argName = createJson()
        .encodeToString(arg)
        .let { Uri.encode(it) }

    return this.replace(oldValue = "{$NavArgKey}", newValue = argName, ignoreCase = false)
}

/**
 *  네비게이션 쿼리 아큐먼트 추출하기
 */
inline fun <reified T: Serializable> Bundle.getNavArg(): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getSerializable(NavArgKey, T::class.java)
    } else {
        this.getSerializable(NavArgKey) as? T
    }
}

/**
 * 커스텀 [NavType] 생성
 */
inline fun <reified T: Serializable> createLegacyNavType(
    isNullableAllowed: Boolean = false
): NavType<T> {
    return object : NavType<T>(isNullableAllowed) {
        override fun put(bundle: Bundle, key: String, value: T) {
            bundle.putSerializable(key, value)
        }

        override fun get(bundle: Bundle, key: String): T? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getSerializable(key, T::class.java)
            } else {
                bundle.getSerializable(key) as? T
            }
        }

        override fun parseValue(value: String): T {
            return createJson().decodeFromString(value)
        }
    }
}


inline fun <reified T: @KotlinSerializable Any> createTypeSafeNavType(
    isNullableAllowed: Boolean = false
): NavType<T> {
    return object : NavType<T>(isNullableAllowed) {
        override fun put(bundle: Bundle, key: String, value: T) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun get(bundle: Bundle, key: String): T? {
            return bundle.getString(key)?.let { 
                Json.decodeFromString<T>(it) 
            }
        }

        override fun parseValue(value: String): T {
            return Json.decodeFromString(value)
        }
    }
}

inline fun <reified T: @KotlinSerializable Any> NavController.navigateTypeSafe(route: T) {
    this.navigate(route)
}

inline fun <reified T: Serializable> NavController.navigateLegacy(route: String, args: T) {
    this.navigate(route.asNavArg(args))
}

fun createJson(): Json {
    return Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
}