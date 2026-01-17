package com.calyx.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.calyx.app.data.models.CallerStats
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.ui.theme.*
import com.calyx.app.utils.DurationFormatter

/**
 * Ranked list item with glass card effect for ranks 4+.
 * 
 * Design Spec:
 * - Background: Glass effect with linear gradient
 * - Border: 1dp, rgba(76, 198, 81, 0.25)
 * - Corner radius: 18dp
 * - Margin: 8dp vertical, 16dp horizontal
 * - Padding: 16dp
 * - Backdrop blur: 20px
 * - Shadow: 0 4dp 16dp rgba(28, 156, 112, 0.08)
 * 
 * Hover/Press state:
 * - Background: rgba(139, 216, 82, 0.15)
 * - Border: rgba(76, 198, 81, 0.4)
 * - Scale: 0.98
 */
@Composable
fun RankedListItem(
    callerStats: CallerStats,
    category: RankingCategory,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color(0x268BD852) else Color.Transparent,
        animationSpec = tween(durationMillis = 150),
        label = "bgColor"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isPressed) Color(0x664EC651) else Color(0x404EC651),
        animationSpec = tween(durationMillis = 150),
        label = "borderColor"
    )
    
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 6f else 4f,
        animationSpec = tween(durationMillis = 150),
        label = "elevation"
    )

    val rank = callerStats.getRank(category)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = ShadowLevel1,
                spotColor = ShadowLevel2
            )
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.08f)
                    )
                )
            )
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(Spacing.listItem)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank number
            Box(
                modifier = Modifier.width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = RankNumberStyle,
                    color = ForestGreen
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Profile image with border
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = Color(0x664EC651), // rgba(76, 198, 81, 0.4)
                        shape = CircleShape
                    )
                    .shadow(
                        elevation = 2.dp,
                        shape = CircleShape,
                        ambientColor = Color(0x1A000000),
                        spotColor = Color(0x1A000000)
                    )
            ) {
                ProfileImage(
                    photoUri = callerStats.profilePhotoUri,
                    displayName = callerStats.displayName,
                    size = 48.dp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Caller info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = callerStats.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = PrimaryText
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Stats row
                Text(
                    text = getSecondaryText(callerStats, category),
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Primary metric
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = getPrimaryMetric(callerStats, category),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = DeepGreen
                )
                Text(
                    text = getMetricLabel(category),
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }
        }
    }
}

private fun getPrimaryMetric(caller: CallerStats, category: RankingCategory): String {
    return when (category) {
        RankingCategory.MOST_CALLED -> "${caller.totalCalls}"
        RankingCategory.MOST_TALKED -> DurationFormatter.formatShort(caller.totalDuration)
    }
}

private fun getMetricLabel(category: RankingCategory): String {
    return when (category) {
        RankingCategory.MOST_CALLED -> "calls"
        RankingCategory.MOST_TALKED -> "total"
    }
}

private fun getSecondaryText(caller: CallerStats, category: RankingCategory): String {
    return when (category) {
        RankingCategory.MOST_CALLED -> {
            val parts = mutableListOf<String>()
            if (caller.incomingCalls > 0) parts.add("${caller.incomingCalls} in")
            if (caller.outgoingCalls > 0) parts.add("${caller.outgoingCalls} out")
            if (caller.missedCalls > 0) parts.add("${caller.missedCalls} missed")
            parts.joinToString(" • ")
        }
        RankingCategory.MOST_TALKED -> {
            val avg = DurationFormatter.formatAverage(caller.totalDuration, caller.totalCalls)
            "${caller.totalCalls} calls • $avg"
        }
    }
}
