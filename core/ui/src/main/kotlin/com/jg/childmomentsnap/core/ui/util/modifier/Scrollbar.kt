package com.jg.childmomentsnap.core.ui.util.modifier

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.scrollbar(
    state: LazyListState,
    width: Dp = 4.dp,
    color: Color = Color.Gray,
    alpha: Float = 0.5f,
    knobCornerRadius: Dp = 4.dp
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) alpha else 0f
    val duration = if (state.isScrollInProgress) 150 else 500

    val animatedAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration),
        label = "ScrollbarAlpha"
    )

    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.firstVisibleItemIndex
        val needDrawScrollbar = state.isScrollInProgress || animatedAlpha > 0.0f

        // Draw scrollbar if needed
        if (needDrawScrollbar) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            // Correction logic if logic above is too simple (often it is for varied height items)
            // Better approach using layoutInfo offsets:
            val layoutInfo = state.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) return@drawWithContent

            val viewportHeight = size.height
            val contentHeight = layoutInfo.totalItemsCount.toFloat() * (layoutInfo.visibleItemsInfo.firstOrNull()?.size?.toFloat() ?: 0f) // Estimation
            // Real estmation is hard with lazy, but we can use simple ratio based on index for scrollbar knob position
            
            // Simplified logic:
            // This is a basic implementation. For true proportional scrollbar we need known total height.
            // But for simple indication: 
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@drawWithContent
            
            val totalCount = layoutInfo.totalItemsCount
            
            // Allow scrollbar to span viewport
            // Knob size ratio
            val viewportSize = layoutInfo.viewportSize.height
            // We don't know total content height easily in LazyList without checking all items.
            // Standard Android behavior often estimates.
            
            // Let's use simple logic: 
            // Knob height = viewport * (visibleCount / totalCount). Min height apply.
            val visibleCount = visibleItems.size
            val knobHeight = (viewportHeight * (visibleCount.toFloat() / totalCount.toFloat())).coerceAtLeast(viewportHeight * 0.1f)
            
            // Scroll offset
            val firstVisibleItem = visibleItems.first()
            val firstVisibleIndex = firstVisibleItem.index
            val itemScrollOffset = firstVisibleItem.offset
            
            // Fraction scrolled
            // This is rough estimation
            val scrollFraction = firstVisibleIndex.toFloat() / totalCount.toFloat()
            val scrollbarY = viewportHeight * scrollFraction
            
            drawRect(
                color = color,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarY),
                size = Size(width.toPx(), knobHeight),
                alpha = animatedAlpha
            )
        }
    }
}
