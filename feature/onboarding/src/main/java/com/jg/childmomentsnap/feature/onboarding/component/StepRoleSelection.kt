package com.jg.childmomentsnap.feature.onboarding.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jg.childmomentsnap.core.model.enums.RoleType
import com.jg.childmomentsnap.core.ui.theme.DarkAccent
import com.jg.childmomentsnap.core.ui.theme.Gray100
import com.jg.childmomentsnap.core.ui.theme.Gray500
import com.jg.childmomentsnap.core.ui.theme.Gray900
import com.jg.childmomentsnap.core.ui.theme.PrimaryAmber
import com.jg.childmomentsnap.core.ui.theme.SelectedBg
import com.jg.childmomentsnap.feature.onboarding.R
import com.jg.childmomentsnap.feature.onboarding.model.RoleItem

@Composable
internal fun StepRoleSelection(
    modifier: Modifier = Modifier,
    roles: List<RoleItem>,
    selectedRole: String?,
    customRole: String,
    onCustomRoleChange: (String) -> Unit,
    onRoleSelect: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.feature_onboarding_role_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900,
            lineHeight = 34.sp
        )

        Text(
            text = stringResource(R.string.feature_onboarding_role_guide_msg),
            fontSize = 14.sp,
            color = Gray500
        )
        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                roles.take(2).forEach { role ->
                    RoleCard(
                        modifier = Modifier.weight(1f),
                        role = role,
                        isSelected = selectedRole == role.roleName,
                        onClick = { onRoleSelect(role.roleName) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                roles.drop(2).forEach { role ->
                    if (role.emoji.isNotEmpty()) {
                        RoleCard(
                            modifier = Modifier.weight(1f),
                            role = role,
                            isSelected = selectedRole == role.roleName,
                            onClick = { onRoleSelect(role.roleName) }
                        )
                    }
                }
            }

            // 기타 직접 입력
            val isOtherSelected = selectedRole == RoleType.OTHER.role
            // 부드러운 색상 전환 애니메이션
            val bgColor by animateColorAsState(if (isOtherSelected) SelectedBg.copy(alpha = 0.5f) else Color.White)
            val borderColor by animateColorAsState(if (isOtherSelected) PrimaryAmber else Gray100)
            val scale by animateFloatAsState(if (isOtherSelected) 1.02f else 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor)
                    .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onRoleSelect(RoleType.OTHER.role) }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "👤", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))

                        if (isOtherSelected) {
                            BasicTextField(
                                value = customRole,
                                onValueChange = onCustomRoleChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkAccent
                                ),
                                cursorBrush = SolidColor(PrimaryAmber),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                decorationBox = { innerTextField ->
                                    Box {
                                        if (customRole.isEmpty()) {
                                            Text(
                                                text = stringResource(R.string.feature_onboarding_role_placeholder),
                                                color = PrimaryAmber.copy(alpha = 0.6f),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.feature_onboarding_role_other_input),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}
