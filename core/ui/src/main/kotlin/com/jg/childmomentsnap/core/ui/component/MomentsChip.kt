package com.jg.childmomentsnap.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jg.childmomentsnap.core.ui.theme.MomentsShapes
import com.jg.childmomentsnap.core.ui.theme.Stone100
import com.jg.childmomentsnap.core.ui.theme.Stone400

/**
 * AI 분석 태그 등을 표시하기 위한 공통 칩 컴포넌트
 */
@Composable
fun MomentsTagChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "#$text",
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Stone400
        ),
        modifier = modifier
            .background(Stone100, MomentsShapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
