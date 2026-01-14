package com.jg.childmomentsnap.core.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jg.childmomentsnap.core.ui.theme.MomentsShapes
import com.jg.childmomentsnap.core.ui.theme.Stone900

/**
 * 프로젝트 전반에서 사용되는 둥근 모서리 버튼
 */
@Composable
fun MomentsPrimaryButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = MomentsShapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = Stone900,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}
