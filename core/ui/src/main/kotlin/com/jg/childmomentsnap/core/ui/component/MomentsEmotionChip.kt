package com.jg.childmomentsnap.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jg.childmomentsnap.core.model.ChildEmotion
import com.jg.childmomentsnap.core.ui.model.containerColor
import com.jg.childmomentsnap.core.ui.model.icon
import com.jg.childmomentsnap.core.ui.model.label
import com.jg.childmomentsnap.core.ui.model.mainColor
import androidx.compose.ui.res.stringResource

@Composable
fun MomentsEmotionChip(
    emotion: ChildEmotion,
    modifier: Modifier = Modifier
) {
    Surface(
        color = emotion.containerColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = emotion.icon,
                contentDescription = null,
                tint = emotion.mainColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(emotion.label),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = emotion.mainColor
            )
        }
    }
}
