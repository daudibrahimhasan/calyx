package com.calyx.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.calyx.app.data.models.CallerStats
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.ui.theme.*
import com.calyx.app.utils.DurationFormatter
import kotlinx.coroutines.delay

/**
 * Top 3 podium display with staggered animations.
 */
@Composable
fun TopThreePodium(
    firstPlace: CallerStats?,
    secondPlace: CallerStats?,
    thirdPlace: CallerStats?,
    category: RankingCategory,
    modifier: Modifier = Modifier
) {
    var showSecond by remember { mutableStateOf(false) }
    var showFirst by remember { mutableStateOf(false) }
    var showThird by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showSecond = true
        delay(150)
        showFirst = true
        delay(150)
        showThird = true
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place - Left
        Box(
            modifier = Modifier.weight(0.3f),
            contentAlignment = Alignment.BottomCenter
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = showSecond && secondPlace != null,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                secondPlace?.let {
                    PodiumCard(
                        caller = it,
                        rank = 2,
                        category = category,
                        height = 180.dp,
                        photoSize = 64.dp
                    )
                }
            }
        }

        // 1st Place - Center (Tallest)
        Box(
            modifier = Modifier.weight(0.4f),
            contentAlignment = Alignment.BottomCenter
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = showFirst && firstPlace != null,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                firstPlace?.let {
                    PodiumCard(
                        caller = it,
                        rank = 1,
                        category = category,
                        height = 220.dp,
                        photoSize = 80.dp,
                        showCrown = true
                    )
                }
            }
        }

        // 3rd Place - Right
        Box(
            modifier = Modifier.weight(0.3f),
            contentAlignment = Alignment.BottomCenter
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = showThird && thirdPlace != null,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                thirdPlace?.let {
                    PodiumCard(
                        caller = it,
                        rank = 3,
                        category = category,
                        height = 160.dp,
                        photoSize = 56.dp
                    )
                }
            }
        }
    }
}

/**
 * Individual podium card for a ranked caller.
 */
@Composable
private fun PodiumCard(
    caller: CallerStats,
    rank: Int,
    category: RankingCategory,
    height: androidx.compose.ui.unit.Dp,
    photoSize: androidx.compose.ui.unit.Dp,
    showCrown: Boolean = false,
    modifier: Modifier = Modifier
) {
    val gradient = when (rank) {
        1 -> Brush.verticalGradient(listOf(GoldLight, Gold.copy(alpha = 0.8f), GoldDark.copy(alpha = 0.6f)))
        2 -> Brush.verticalGradient(listOf(SilverLight, Silver.copy(alpha = 0.8f), SilverDark.copy(alpha = 0.6f)))
        3 -> Brush.verticalGradient(listOf(BronzeLight, Bronze.copy(alpha = 0.8f), BronzeDark.copy(alpha = 0.6f)))
        else -> Brush.verticalGradient(listOf(RosePinkLight, RosePink))
    }

    val elevation = when (rank) {
        1 -> 8.dp
        2 -> 4.dp
        else -> 2.dp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Crown for 1st place
        if (showCrown) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Crown",
                modifier = Modifier.size(28.dp),
                tint = Gold
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Profile photo
        ProfileImageWithBadge(
            photoUri = caller.profilePhotoUri,
            displayName = caller.displayName,
            rank = rank,
            size = photoSize,
            badgeSize = (photoSize.value / 3.5f).dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Podium block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height - photoSize - 16.dp)
                .shadow(elevation, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                // Name
                Text(
                    text = caller.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = getOnRankColor(rank),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Metric
                Text(
                    text = when (category) {
                        RankingCategory.MOST_CALLED -> "${caller.totalCalls} calls"
                        RankingCategory.MOST_TALKED -> DurationFormatter.formatShort(caller.totalDuration)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = getOnRankColor(rank).copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                // Rank number
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = getOnRankColor(rank).copy(alpha = 0.3f)
                )
            }
        }
    }
}
