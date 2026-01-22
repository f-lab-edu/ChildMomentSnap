package com.jg.childmomentsnap.core.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.jg.childmomentsnap.core.model.ChildEmotion

import com.jg.childmomentsnap.core.ui.R

val ChildEmotion.label: Int
    get() = when (this) {
        ChildEmotion.HAPPY -> R.string.emotion_happy
        ChildEmotion.CALM -> R.string.emotion_calm
        ChildEmotion.CURIOUS -> R.string.emotion_curious
        ChildEmotion.SURPRISED -> R.string.emotion_surprised
        ChildEmotion.SAD -> R.string.emotion_sad
        ChildEmotion.CRINGE -> R.string.emotion_cringe
    }

val ChildEmotion.icon: ImageVector
    get() = when (this) {
        ChildEmotion.HAPPY -> Icons.Default.SentimentVerySatisfied
        ChildEmotion.CALM -> Icons.Default.SentimentSatisfied
        ChildEmotion.CURIOUS -> Icons.Default.Search
        ChildEmotion.SURPRISED -> Icons.Default.Bolt
        ChildEmotion.SAD -> Icons.Default.SentimentVeryDissatisfied
        ChildEmotion.CRINGE -> Icons.Default.MoodBad
    }

val ChildEmotion.mainColor: Color
    get() = when (this) {
        ChildEmotion.HAPPY -> Color(0xFFF59E0B)
        ChildEmotion.CALM -> Color(0xFF78716C)
        ChildEmotion.CURIOUS -> Color(0xFF0EA5E9)
        ChildEmotion.SURPRISED -> Color(0xFF8B5CF6)
        ChildEmotion.SAD -> Color(0xFFEF4444)
        ChildEmotion.CRINGE -> Color(0xFF10B981)
    }

val ChildEmotion.containerColor: Color
    get() = when (this) {
        ChildEmotion.HAPPY -> Color(0xFFFEF3C7)
        ChildEmotion.CALM -> Color(0xFFF5F5F4)
        ChildEmotion.CURIOUS -> Color(0xFFE0F2FE)
        ChildEmotion.SURPRISED -> Color(0xFFEDE9FE)
        ChildEmotion.SAD -> Color(0xFFFEE2E2)
        ChildEmotion.CRINGE -> Color(0xFFD1FAE5)
    }
