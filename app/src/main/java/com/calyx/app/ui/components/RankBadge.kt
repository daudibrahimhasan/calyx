package com.calyx.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.ui.theme.*

/**
 * Rank badge component with green-themed gradient backgrounds.
 */
@Composable
fun RankBadge(
    rank: Int,
    size: Dp = 20.dp,
    modifier: Modifier = Modifier
) {
    val (gradient, contentColor) = when (rank) {
        1 -> Brush.linearGradient(listOf(LimeAccent, VibrantGreen)) to Color.White
        2 -> Brush.linearGradient(listOf(FreshGreen, ForestGreen)) to PrimaryText
        3 -> Brush.linearGradient(listOf(ForestGreen, DeepGreen)) to Color.White
        else -> Brush.linearGradient(listOf(TealGreen, DeepGreen)) to Color.White
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(gradient)
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (rank in 1..3) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Rank $rank",
                tint = contentColor,
                modifier = Modifier.size(size * 0.6f)
            )
        } else {
            Text(
                text = rank.toString(),
                color = contentColor,
                fontSize = (size.value * 0.5f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Get gradient brush for rank backgrounds.
 */
@Composable
fun getRankGradient(rank: Int): Brush {
    return when (rank) {
        1 -> CalyzGradients.winnerPodiumGradient
        2 -> CalyzGradients.silverPodiumGradient
        3 -> CalyzGradients.bronzePodiumGradient
        else -> CalyzGradients.tealAccentGradient
    }
}

/**
 * Get solid color for rank.
 */
fun getRankColor(rank: Int): Color {
    return when (rank) {
        1 -> LimeAccent
        2 -> FreshGreen
        3 -> DeepGreen
        else -> TealGreen
    }
}

/**
 * Get content color for rank (text on rank background).
 */
fun getOnRankColor(rank: Int): Color {
    return when (rank) {
        1 -> Color.White
        2 -> PrimaryText
        3 -> Color.White
        else -> Color.White
    }
}
