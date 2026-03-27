package com.jg.childmomentsnap.feature.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jg.childmomentsnap.core.ui.theme.Gray100
import com.jg.childmomentsnap.core.ui.theme.Gray300
import com.jg.childmomentsnap.core.ui.theme.Gray500
import com.jg.childmomentsnap.core.ui.theme.Gray900
import com.jg.childmomentsnap.core.ui.theme.PrimaryAmber
import com.jg.childmomentsnap.feature.onboarding.R
import kotlinx.coroutines.delay


@Composable
internal fun StepBabyName(
    babyName: String,
    onNameChange: (String) -> Unit,
    onNextStep: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.feature_onboarding_baby_name_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900,
            lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.feature_onboarding_baby_name_sub_title),
            fontSize = 14.sp,
            color = Gray500
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = babyName,
            onValueChange = onNameChange,
            placeholder = {
                Text(stringResource(R.string.feature_onboarding_baby_name_placeholder), color = Gray300)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = "Baby Icon",
                    tint = PrimaryAmber
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryAmber,
                unfocusedBorderColor = Gray100,
                focusedTextColor = Gray900,
                unfocusedTextColor = Gray900,
                cursorColor = PrimaryAmber
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (babyName.isNotBlank()) {
                        onNextStep()
                    }
                }
            )
        )
    }
}