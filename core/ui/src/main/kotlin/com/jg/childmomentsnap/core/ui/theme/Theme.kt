package com.jg.childmomentsnap.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MomentsTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = lightColorScheme(
        primary = Amber500,
        onPrimary = Color.White,
        primaryContainer = Amber100,
        onPrimaryContainer = Stone900,
        
        secondary = Rose400,
        onSecondary = Color.White,
        
        background = Stone50,
        onBackground = Stone900,
        
        surface = Color.White,
        onSurface = Stone900,
        onSurfaceVariant = Stone400,
        
        outline = Stone100,
        error = Rose400
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MomentsTypography,
        shapes = MomentsShapes,
        content = content
    )
}
