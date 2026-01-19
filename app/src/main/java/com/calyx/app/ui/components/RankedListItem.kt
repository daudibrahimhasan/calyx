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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.data.models.CallerStats
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.ui.theme.*
import com.calyx.app.utils.DurationFormatter
import com.calyx.app.utils.PhoneNumberUtils

/**
 * Compact ranked list item for ranks 4+.
 * 
 * Design:
 * - Height: ~56dp (compact)
 * - Rank: Neon Fern (VibrantGreen) color
 * - Name: Bold, Primary text (Mist White feel)
 * - Score: Pill-shaped badge with translucent green background
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
        animationSpec = tween(100),
        label = "scale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = tween(100),
        label = "bg"
    )

    val rank = callerStats.getRank(category)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank number - Primary color (adapts to theme)
        Text(
            text = "$rank",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(42.dp) // Widen to fit 3 digits (e.g. 100)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Profile image
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            ProfileImage(
                photoUri = callerStats.profilePhotoUri,
                displayName = callerStats.displayName,
                size = 40.dp,
                isUnsavedContact = PhoneNumberUtils.isUnsavedContact(
                    callerStats.displayName,
                    callerStats.phoneNumber
                )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name and subtitle
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = PhoneNumberUtils.getDisplayName(
                    callerStats.displayName, 
                    callerStats.phoneNumber
                ),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = getSubtitle(callerStats, category),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Score pill badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = getScore(callerStats, category),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun getScore(caller: CallerStats, category: RankingCategory): String {
    return when (category) {
        RankingCategory.MOST_CALLED -> "${caller.totalCalls}"
        RankingCategory.MOST_TALKED -> DurationFormatter.formatShort(caller.totalDuration)
    }
}

private fun getSubtitle(caller: CallerStats, category: RankingCategory): String {
    return when (category) {
        RankingCategory.MOST_CALLED -> {
            val parts = mutableListOf<String>()
            if (caller.incomingCalls > 0) parts.add("${caller.incomingCalls}↓")
            if (caller.outgoingCalls > 0) parts.add("${caller.outgoingCalls}↑")
            if (caller.missedCalls > 0) parts.add("${caller.missedCalls}✕")
            parts.joinToString(" ")
        }
        RankingCategory.MOST_TALKED -> {
            val avg = DurationFormatter.formatAverage(caller.totalDuration, caller.totalCalls)
            "${caller.totalCalls} calls • avg $avg"
        }
    }
}
