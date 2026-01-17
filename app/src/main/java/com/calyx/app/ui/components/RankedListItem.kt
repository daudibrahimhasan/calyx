package com.calyx.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.calyx.app.data.models.CallerStats
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.utils.DurationFormatter

/**
 * Single row in the ranked list (for ranks 4+).
 */
@Composable
fun RankedListItem(
    callerStats: CallerStats,
    category: RankingCategory,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "scale"
    )

    val rank = callerStats.getRank(category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank number
            Box(
                modifier = Modifier.width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Profile image
            ProfileImage(
                photoUri = callerStats.profilePhotoUri,
                displayName = callerStats.displayName,
                size = 48.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Caller info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = callerStats.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = getSecondaryText(callerStats, category),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = getMetricLabel(category),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
