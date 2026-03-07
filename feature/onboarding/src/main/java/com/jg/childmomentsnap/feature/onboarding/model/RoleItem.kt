package com.jg.childmomentsnap.feature.onboarding.model

import androidx.annotation.StringRes
import com.jg.childmomentsnap.feature.onboarding.R

data class RoleItem(
    val id: String,
    val emoji: String,
    @StringRes val titleResId: Int = R.string.feat_onboarding_role_mom,
)
