package com.calyx.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.calyx.app.data.models.CallerStats
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.ui.theme.*
import com.calyx.app.utils.DurationFormatter
import com.calyx.app.utils.PhoneNumberUtils
import kotlinx.coroutines.delay

/**
 * Top 3 podium with glassmorphism effect.
 * 
 * Layout:
 *   #2 (Left)    #1 (Center)    #3 (Right)
 *   70% height   100% height    60% height
 *   20% opacity  30% opacity    15% opacity
 * 
 * Profile pictures positioned 50% above card, 50% overlapping.
 */
@Composable
fun TopThreePodium(
    firstPlace: CallerStats?,
    secondPlace: CallerStats?,
    thirdPlace: CallerStats?,
    category: RankingCategory,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Staggered entrance animation states
    var showSecond by remember { mutableStateOf(false) }
    var showFirst by remember { mutableStateOf(false) }
    var showThird by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showSecond = true
        delay(100)
        showFirst = true
        delay(150)
        showThird = true
    }

    // Fixed podium dimensions
    val winnerCardHeight = 155.dp // Increased from 140dp
    val winnerAvatarSize = 72.dp
    val secondCardHeight = winnerCardHeight * 0.70f  // 70% of winner
    val secondAvatarSize = 60.dp
    val thirdCardHeight = winnerCardHeight * 0.60f   // 60% of winner
    val thirdAvatarSize = 54.dp
    
    // Total height = card height + half of avatar (avatar overlaps 50%)
    val totalHeight = winnerCardHeight + (winnerAvatarSize / 2) + 16.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight)
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // #2 - Left position (Silver)
            Box(
                modifier = Modifier
                    .weight(0.30f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                secondPlace?.let {
                    GlassPodiumCard(
                        caller = it,
                        rank = 2,
                        category = category,
                        isDarkTheme = isDarkTheme,
                        cardHeight = secondCardHeight,
                        avatarSize = secondAvatarSize,
                        isVisible = showSecond,
                        glassOpacity = 0.20f,
                        glowColor = FreshGreen
                    )
                }
            }

            // #1 - Center position (Gold/Winner)
            Box(
                modifier = Modifier
                    .weight(0.40f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                firstPlace?.let {
                    GlassPodiumCard(
                        caller = it,
                        rank = 1,
                        category = category,
                        isDarkTheme = isDarkTheme,
                        cardHeight = winnerCardHeight,
                        avatarSize = winnerAvatarSize,
                        isVisible = showFirst,
                        glassOpacity = 0.30f,
                        glowColor = LimeAccent,
                        showGlowRing = true
                    )
                }
            }

            // #3 - Right position (Bronze)
            Box(
                modifier = Modifier
                    .weight(0.30f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                thirdPlace?.let {
                    GlassPodiumCard(
                        caller = it,
                        rank = 3,
                        category = category,
                        isDarkTheme = isDarkTheme,
                        cardHeight = thirdCardHeight,
                        avatarSize = thirdAvatarSize,
                        isVisible = showThird,
                        glassOpacity = 0.15f,
                        glowColor = DeepGreen
                    )
                }
            }
        }
    }
}

/**
 * Individual glassmorphism podium card.
 * Profile picture is positioned 50% above the card, 50% overlapping onto it.
 */
@Composable
private fun GlassPodiumCard(
    caller: CallerStats,
    rank: Int,
    category: RankingCategory,
    isDarkTheme: Boolean,
    cardHeight: Dp,
    avatarSize: Dp,
    isVisible: Boolean,
    glassOpacity: Float,
    glowColor: Color,
    showGlowRing: Boolean = false
) {
    // Animation states
    val animatedHeight by animateDpAsState(
        targetValue = if (isVisible) cardHeight else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardHeight"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "alpha"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Pulsing glow for winner
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Profile overlaps 50% above card
    val avatarOffset = avatarSize / 2

    // Container for proper layering - total height is card + half avatar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight + avatarOffset)
            .graphicsLayer {
                alpha = animatedAlpha
                scaleX = animatedScale
                scaleY = animatedScale
            },
        contentAlignment = Alignment.TopCenter
    ) {
        // LAYER 1: Glass Card (lower z-index, at bottom)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        ) {
            // Glass card background
            GlassCardContent(
                caller = caller,
                rank = rank,
                category = category,
                isDarkTheme = isDarkTheme,
                glassOpacity = glassOpacity,
                glowColor = glowColor,
                topPadding = avatarOffset + 8.dp // Space for avatar overlap + padding
            )
        }

        // LAYER 2: Profile Avatar (higher z-index, floating above)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(2f)
        ) {
            // Glow ring behind avatar for winner
            if (showGlowRing) {
                Box(
                    modifier = Modifier
                        .size(avatarSize + 20.dp)
                        .align(Alignment.Center)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        LimeAccent.copy(alpha = glowAlpha * 0.6f),
                                        LimeAccent.copy(alpha = glowAlpha * 0.3f),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.minDimension / 2
                            )
                        }
                )
            }

            // Avatar container - includes both the profile image and the rank badge
            Box(
                modifier = Modifier.size(avatarSize),
                contentAlignment = Alignment.Center
            ) {
                // Avatar with shadow for floating effect
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 12.dp,
                            shape = CircleShape,
                            ambientColor = Color.Black.copy(alpha = 0.25f),
                            spotColor = Color.Black.copy(alpha = 0.25f)
                        )
                        .clip(CircleShape)
                        .border(
                            width = if (rank == 1) 3.dp else 2.dp,
                            color = Color.White.copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                ) {
                    ProfileImage(
                        photoUri = caller.profilePhotoUri,
                        displayName = caller.displayName,
                        size = avatarSize,
                        isUnsavedContact = PhoneNumberUtils.isUnsavedContact(
                            caller.displayName,
                            caller.phoneNumber
                        )
                    )
                }

                // Rank badge - Positioned at bottom-right corner of avatar
                val badgeSize = if (rank == 1) 22.dp else 20.dp
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(badgeSize)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(getRankBadgeColor(rank)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$rank",
                        fontSize = if (rank == 1) 11.sp else 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Glassmorphism card content with frosted glass effect.
 */
@Composable
private fun GlassCardContent(
    caller: CallerStats,
    rank: Int,
    category: RankingCategory,
    isDarkTheme: Boolean,
    glassOpacity: Float,
    glowColor: Color,
    topPadding: Dp
) {
    val cardShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    
    // Glassmorphism gradient brush
    val glassGradient = Brush.linearGradient(
        colors = listOf(
            glowColor.copy(alpha = glassOpacity + 0.1f),
            glowColor.copy(alpha = glassOpacity),
            glowColor.copy(alpha = glassOpacity - 0.05f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                elevation = 8.dp,
                shape = cardShape,
                ambientColor = glowColor.copy(alpha = 0.15f),
                spotColor = glowColor.copy(alpha = 0.2f)
            )
            .clip(cardShape)
            // Glass background
            .background(glassGradient)
            // Semi-transparent white overlay for frosted effect
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.10f)
                    )
                )
            )
            // Subtle border for glass edge
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.15f)
                    )
                ),
                shape = cardShape
            )
    ) {
        // Card content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding, start = 8.dp, end = 8.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Name - Theme-aware high contrast
            Text(
                text = PhoneNumberUtils.getDisplayName(caller.displayName, caller.phoneNumber),
                fontSize = when (rank) {
                    1 -> 14.sp
                    2 -> 12.sp
                    else -> 11.sp
                },
                fontWeight = FontWeight.SemiBold,
                // Fix for hard to read text in Night Mode: Use pure White for dark theme instead of muted mint
                color = if (isDarkTheme) Color.White else DarkForest,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Score with glassmorphism pill - Theme-aware colors
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                         if (isDarkTheme) LimeAccent.copy(alpha = 0.3f) 
                         else Color.White.copy(alpha = 0.35f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isDarkTheme) LimeAccent.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when (category) {
                        RankingCategory.MOST_CALLED -> "${caller.totalCalls} calls"
                        RankingCategory.MOST_TALKED -> DurationFormatter.formatShort(caller.totalDuration)
                    },
                    fontSize = when (rank) {
                        1 -> 13.sp
                        2 -> 11.sp
                        else -> 10.sp
                    },
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else ForestGreen
                )
            }
        }
    }
}

/**
 * Get rank badge background color.
 */
private fun getRankBadgeColor(rank: Int): Color {
    return when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> VibrantGreen
    }
}
