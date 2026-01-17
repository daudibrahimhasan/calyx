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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.calyx.app.utils.PhoneNumberUtils
import kotlinx.coroutines.delay

/**
 * Top 3 podium with "Growth" animation - columns rise from bottom like trees.
 * 
 * Layout:
 *   #2 (Left)    #1 (Center)    #3 (Right)
 *   Medium       Tallest        Shortest
 *   Silver       Gold+Glow      Bronze
 */
@Composable
fun TopThreePodium(
    firstPlace: CallerStats?,
    secondPlace: CallerStats?,
    thirdPlace: CallerStats?,
    category: RankingCategory,
    modifier: Modifier = Modifier
) {
    // Staggered "growth" animation states
    var growSecond by remember { mutableStateOf(false) }
    var growFirst by remember { mutableStateOf(false) }
    var growThird by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        growSecond = true
        delay(100)
        growFirst = true
        delay(150)
        growThird = true
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // #2 - Left (Silver)
            Box(
                modifier = Modifier.weight(0.28f),
                contentAlignment = Alignment.BottomCenter
            ) {
                secondPlace?.let {
                    GrowthPodiumCard(
                        caller = it,
                        rank = 2,
                        category = category,
                        targetHeight = 130.dp,
                        avatarSize = 56.dp,
                        isVisible = growSecond,
                        accentColor = FreshGreen,
                        medalColor = Silver
                    )
                }
            }

            // #1 - Center (Gold with Glow)
            Box(
                modifier = Modifier.weight(0.44f),
                contentAlignment = Alignment.BottomCenter
            ) {
                firstPlace?.let {
                    GrowthPodiumCard(
                        caller = it,
                        rank = 1,
                        category = category,
                        targetHeight = 160.dp,
                        avatarSize = 72.dp,
                        isVisible = growFirst,
                        accentColor = LimeAccent,
                        medalColor = Gold,
                        showGlowRing = true
                    )
                }
            }

            // #3 - Right (Bronze)
            Box(
                modifier = Modifier.weight(0.28f),
                contentAlignment = Alignment.BottomCenter
            ) {
                thirdPlace?.let {
                    GrowthPodiumCard(
                        caller = it,
                        rank = 3,
                        category = category,
                        targetHeight = 110.dp,
                        avatarSize = 52.dp,
                        isVisible = growThird,
                        accentColor = DeepGreen,
                        medalColor = Bronze
                    )
                }
            }
        }
    }
}

/**
 * Individual podium card with growth animation.
 */
@Composable
private fun GrowthPodiumCard(
    caller: CallerStats,
    rank: Int,
    category: RankingCategory,
    targetHeight: Dp,
    avatarSize: Dp,
    isVisible: Boolean,
    accentColor: Color,
    medalColor: Color,
    showGlowRing: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Growth animation
    val animatedHeight by animateDpAsState(
        targetValue = if (isVisible) targetHeight else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "height"
    )
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "alpha"
    )

    // Glow animation for #1
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = animatedAlpha },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with optional glow ring
        Box(
            modifier = Modifier.offset(y = avatarSize / 3),
            contentAlignment = Alignment.Center
        ) {
            // Glow ring for #1
            if (showGlowRing) {
                Box(
                    modifier = Modifier
                        .size(avatarSize + 16.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        LimeAccent.copy(alpha = glowAlpha),
                                        LimeAccent.copy(alpha = glowAlpha * 0.5f),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.minDimension / 2
                            )
                        }
                )
            }
            
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(
                        width = if (rank == 1) 3.dp else 2.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .shadow(4.dp, CircleShape)
            ) {
                ProfileImage(
                    photoUri = caller.profilePhotoUri,
                    displayName = caller.displayName,
                    size = avatarSize
                )
            }
            
            // Medal badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size(if (rank == 1) 24.dp else 20.dp)
                    .clip(CircleShape)
                    .background(medalColor)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    fontSize = if (rank == 1) 11.sp else 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 2) PrimaryText else Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Podium block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor,
                            accentColor.copy(alpha = 0.7f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(avatarSize / 3))
                
                // Name (masked if unknown)
                Text(
                    text = PhoneNumberUtils.getDisplayName(caller.displayName, caller.phoneNumber),
                    fontSize = if (rank == 1) 13.sp else 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (rank == 2) PrimaryText else Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Score pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (rank == 2) DeepGreen.copy(alpha = 0.2f) 
                            else Color.White.copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = when (category) {
                            RankingCategory.MOST_CALLED -> "${caller.totalCalls}"
                            RankingCategory.MOST_TALKED -> DurationFormatter.formatShort(caller.totalDuration)
                        },
                        fontSize = if (rank == 1) 14.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (rank == 2) DeepGreen else Color.White
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.Modifier.graphicsLayer(block: androidx.compose.ui.graphics.GraphicsLayerScope.() -> Unit): Modifier {
    return this.then(
        Modifier.graphicsLayer(block)
    )
}
