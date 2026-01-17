package com.calyx.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calyx.app.data.models.TimeRange
import com.calyx.app.ui.theme.*

/**
 * Time filter styled as horizontal pill buttons.
 * 
 * Design Spec:
 * - Background: rgba(255, 255, 255, 0.2)
 * - Border: 1dp, rgba(139, 216, 82, 0.3)
 * - Corner Radius: 20dp (fully rounded pills)
 * - Active: White background, #1C9C70 text, SemiBold
 * - Inactive: Transparent background, White (80% opacity), Regular
 */
@Composable
fun TimeFilterTabs(
    selectedRange: TimeRange,
    onRangeChange: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val ranges = TimeRange.entries
    val selectedIndex = ranges.indexOf(selectedRange)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenEdge, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        ranges.forEachIndexed { index, range ->
            val isSelected = selectedIndex == index
            
            TimeFilterPill(
                text = range.displayName,
                isSelected = isSelected,
                onClick = { onRangeChange(range) }
            )
        }
    }
}

@Composable
private fun TimeFilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
        animationSpec = tween(durationMillis = 200),
        label = "bgColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) DeepGreen else Color.White.copy(alpha = 0.8f),
        animationSpec = tween(durationMillis = 200),
        label = "textColor"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) DeepGreen.copy(alpha = 0.3f) else Color(0x4D8BD852),
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(CornerRadius.round),
                        ambientColor = ShadowLevel1,
                        spotColor = ShadowLevel1
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(CornerRadius.round))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(CornerRadius.round)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}
