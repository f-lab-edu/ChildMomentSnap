package com.jg.childmomentsnap.core.common.android.provider

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.jg.childmomentsnap.core.common.provider.AppInfoProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import javax.inject.Inject

class AndroidAppInfoProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : AppInfoProvider {

    override fun getPackageName(): String = context.packageName

    override fun getCertificateFingerprint(): String? {
        val signatures = try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }
        } catch (e: Exception) {
            return null
        }

        if (signatures != null && signatures.isNotEmpty()) {
            try {
                val md = MessageDigest.getInstance("SHA1")
                val signature = signatures[0].toByteArray()
                val digest = md.digest(signature)
                return digest.joinToString(":") { "%02X".format(it) }
            } catch (e: Exception) {
                // Ignore key extraction error
            }
        }
        return null
    }
}
