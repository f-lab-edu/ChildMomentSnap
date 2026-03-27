package com.jg.childmomentsnap.feature.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jg.childmomentsnap.feature.onboarding.R
import com.jg.childmomentsnap.core.ui.theme.DarkAccent
import com.jg.childmomentsnap.core.ui.theme.Gray100
import com.jg.childmomentsnap.core.ui.theme.Gray300
import com.jg.childmomentsnap.core.ui.theme.Gray500
import com.jg.childmomentsnap.core.ui.theme.Gray900
import com.jg.childmomentsnap.core.ui.theme.PrimaryAmber

@Composable
internal fun StepBirthDate(
    babyName: String,
    birthDate: String,
    onDateChange: (String) -> Unit,
    isPregnant: Boolean,
    onPregnantChange: (Boolean) -> Unit,
    onComplete: () -> Unit
) {
    val displayName = babyName.ifBlank { "아이" }

    Column(modifier =
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.feature_onboarding_birthday_title, displayName),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900,
            lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.feature_onboarding_birthday_sub_title),
            fontSize = 14.sp,
            color = Gray500
        )
        Spacer(modifier = Modifier.height(32.dp))


        OutlinedTextField(
            value = birthDate,
            onValueChange = onDateChange,
            placeholder = {
                Text(stringResource(R.string.feature_onboarding_birthday_placeholder), color = Gray300)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Calendar Icon",
                    tint = if (isPregnant) Gray300 else PrimaryAmber
                )
            },
            enabled = !isPregnant,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF9FAFB),
                focusedBorderColor = PrimaryAmber,
                unfocusedBorderColor = Gray100,
                disabledBorderColor = Gray100,
                focusedTextColor = Gray900,
                unfocusedTextColor = Gray900,
                disabledTextColor = Gray300
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (birthDate.isNotBlank() || isPregnant) {
                        onComplete()
                    }
                }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))


        Row(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onPregnantChange(!isPregnant) }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(
                        width = 2.dp,
                        color = if (isPregnant) PrimaryAmber else Gray300,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .background(if (isPregnant) PrimaryAmber else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isPregnant) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Checked",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.feature_onboarding_birthday_pregnant),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isPregnant) DarkAccent else Gray500
            )
        }
    }
}