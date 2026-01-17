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
 * Rank badge component showing medal colors for top 3.
 */
@Composable
fun RankBadge(
    rank: Int,
    size: Dp = 20.dp,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = when (rank) {
        1 -> Gold to OnGold
        2 -> Silver to OnSilver
        3 -> Bronze to OnBronze
        else -> RosePink to Color.White
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
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
 * Get gradient brush for rank.
 */
@Composable
fun getRankGradient(rank: Int): Brush {
    return when (rank) {
        1 -> Brush.verticalGradient(listOf(GoldLight, Gold, GoldDark))
        2 -> Brush.verticalGradient(listOf(SilverLight, Silver, SilverDark))
        3 -> Brush.verticalGradient(listOf(BronzeLight, Bronze, BronzeDark))
        else -> Brush.verticalGradient(listOf(RosePinkLight, RosePink, RosePinkDark))
    }
}

/**
 * Get solid color for rank.
 */
fun getRankColor(rank: Int): Color {
    return when (rank) {
        1 -> Gold
        2 -> Silver
        3 -> Bronze
        else -> RosePink
    }
}

/**
 * Get content color for rank (text on rank background).
 */
fun getOnRankColor(rank: Int): Color {
    return when (rank) {
        1 -> OnGold
        2 -> OnSilver
        3 -> OnBronze
        else -> Color.White
    }
}
