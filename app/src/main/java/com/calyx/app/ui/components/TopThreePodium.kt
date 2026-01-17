package com.calyx.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.data.models.CallerStats
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.ui.theme.*
import com.calyx.app.utils.DurationFormatter
import kotlinx.coroutines.delay

/**
 * Top 3 podium display with staggered bounce animations.
 * 
 * Design Spec Layout:
 *      #2        #1        #3
 *     ┌──┐     ┌───┐     ┌──┐
 *     │  │     │   │     │  │
 *     │  │     │   │     │  │
 *     └──┘     │   │     └──┘
 *              └───┘
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
        delay(100)
        showFirst = true
        delay(200)
        showThird = true
    }

    // Subtle radial gradient background
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(CalyxGradients.podiumContainerGradient)
            .padding(top = 32.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // 2nd Place - Left (Silver)
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
                            height = 140.dp,
                            width = 120.dp,
                            avatarSize = 70.dp
                        )
                    }
                }
            }

            // 1st Place - Center (Winner/Gold)
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
                            height = 180.dp,
                            width = 140.dp,
                            avatarSize = 90.dp,
                            showCrown = true
                        )
                    }
                }
            }

            // 3rd Place - Right (Bronze)
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
                            height = 140.dp,
                            width = 120.dp,
                            avatarSize = 70.dp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual podium card with gradient and glass overlay.
 */
@Composable
private fun PodiumCard(
    caller: CallerStats,
    rank: Int,
    category: RankingCategory,
    height: Dp,
    width: Dp,
    avatarSize: Dp,
    showCrown: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Get gradient based on rank
    val gradient = when (rank) {
        1 -> CalyxGradients.winnerPodiumGradient
        2 -> CalyxGradients.silverPodiumGradient
        else -> CalyxGradients.bronzePodiumGradient
    }

    // Podium properties based on rank
    val (borderWidth, borderColor, textColor, rankTextAlpha) = when (rank) {
        1 -> Quadruple(
            2.5.dp,
            Color.White.copy(alpha = 0.5f),
            Color.White,
            0.25f
        )
        2 -> Quadruple(
            2.dp,
            Color.White.copy(alpha = 0.4f),
            PrimaryText,
            0.15f
        )
        else -> Quadruple(
            2.dp,
            Color.White.copy(alpha = 0.4f),
            Color.White,
            0.2f
        )
    }

    val shadowColor = when (rank) {
        1 -> Color(0x4D4EC651) // rgba(76, 198, 81, 0.3)
        2 -> Color(0x4089D885) // rgba(137, 216, 133, 0.25)
        else -> Color(0x401C9C70) // rgba(28, 156, 112, 0.25)
    }

    // Winner glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Column(
        modifier = modifier.width(width),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Crown for winner
        if (showCrown) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Winner",
                modifier = Modifier.size(32.dp),
                tint = LimeAccent
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Avatar (floating above podium)
        Box(
            modifier = Modifier
                .offset(y = avatarSize / 2 + 8.dp)
                .then(
                    if (rank == 1) {
                        Modifier.shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = GlowWinner.copy(alpha = glowAlpha * 0.4f),
                            spotColor = GlowWinner.copy(alpha = glowAlpha * 0.4f)
                        )
                    } else Modifier
                )
        ) {
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(
                        width = if (rank == 1) 4.dp else 3.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        ambientColor = Color(0x26000000),
                        spotColor = Color(0x26000000)
                    )
            ) {
                ProfileImage(
                    photoUri = caller.profilePhotoUri,
                    displayName = caller.displayName,
                    size = avatarSize
                )
            }
        }

        // Podium block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .then(
                    if (rank == 1) {
                        Modifier.shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(CornerRadius.large),
                            ambientColor = shadowColor,
                            spotColor = shadowColor
                        )
                    } else {
                        Modifier.shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(CornerRadius.large),
                            ambientColor = shadowColor,
                            spotColor = shadowColor
                        )
                    }
                )
                .clip(RoundedCornerShape(CornerRadius.large))
                .background(gradient)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(CornerRadius.large)
                )
        ) {
            // Glass overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (rank == 1) 0.25f else 0.2f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = 100f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = avatarSize / 2 + 16.dp,
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 12.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Name
                Text(
                    text = caller.displayName,
                    style = if (rank == 1) WinnerNameStyle else RunnerUpNameStyle,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                // Count/Metric
                Text(
                    text = when (category) {
                        RankingCategory.MOST_CALLED -> "${caller.totalCalls} calls"
                        RankingCategory.MOST_TALKED -> DurationFormatter.formatShort(caller.totalDuration)
                    },
                    style = if (rank == 1) PodiumCountStyle else MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = if (rank == 2) DeepGreen else textColor.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                // Label
                Text(
                    text = when (category) {
                        RankingCategory.MOST_CALLED -> "calls"
                        RankingCategory.MOST_TALKED -> "total"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = if (rank == 2) 0.7f else 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                // Background rank number
                Text(
                    text = "$rank",
                    style = if (rank == 1) WinnerRankStyle else RunnerUpRankStyle,
                    color = textColor.copy(alpha = rankTextAlpha),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Helper data class for podium properties
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component1() = first
private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component2() = second
private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component3() = third
private operator fun <A, B, C, D> Quadruple<A, B, C, D>.component4() = fourth
