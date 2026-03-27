package com.jg.childmomentsnap.feature.onboarding.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jg.childmomentsnap.core.ui.theme.DarkAccent
import com.jg.childmomentsnap.core.ui.theme.Gray100
import com.jg.childmomentsnap.core.ui.theme.PrimaryAmber
import com.jg.childmomentsnap.core.ui.theme.SelectedBg
import com.jg.childmomentsnap.feature.onboarding.model.RoleItem


@Composable
fun RoleCard(
    modifier: Modifier = Modifier,
    role: RoleItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isSelected) 1.02f else 1f, label = "scaleAnimation")

    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) SelectedBg.copy(alpha = 0.5f) else Color.White)
            .border(
                2.dp,
                if (isSelected) PrimaryAmber else Gray100,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = role.emoji, fontSize = 40.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(role.titleResId),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) DarkAccent else Color.DarkGray
        )
    }
}