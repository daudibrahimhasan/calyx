package com.calyx.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.calyx.app.ui.theme.*

/**
 * Empty state displayed when no calls are found.
 * 
 * Design Spec:
 * - Color: #BCE8C7 (SoftGreen)
 * - Style: Minimal line art
 * - Message: Lufga Regular, 16sp, #1C6F54
 * - Subtext: Lufga Light, 14sp, #1C6F54 60%
 */
@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PhoneDisabled,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = SoftGreen
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = SecondaryText.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Loading state with custom green spinner and shimmer effect.
 * 
 * Design Spec:
 * - Spinner Color: #4EC651
 * - Size: 40dp
 * - Stroke: 3dp
 * - Background: glass card with blur
 */
@Composable
fun LoadingState(
    message: String = "Analyzing your calls...",
    modifier: Modifier = Modifier
) {
    // Rotation animation for spinner
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Scale pulse animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glass card container for spinner
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(CornerRadius.xLarge))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                        scaleX = scale
                        scaleY = scale
                    },
                color = VibrantGreen,
                strokeWidth = 3.dp,
                trackColor = SoftGreen.copy(alpha = 0.3f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Shimmer loading skeleton with green gradient.
 */
@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )
    
    val shimmerGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0x33BCE8C7), // rgba(188, 232, 199, 0.2)
            Color(0x4D8BD852), // rgba(139, 216, 82, 0.3)
            Color(0x33BCE8C7)
        ),
        startX = shimmerProgress * 300f,
        endX = shimmerProgress * 300f + 200f
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(shimmerGradient)
    )
}

/**
 * Error state with action button.
 * 
 * Design Spec:
 * - Background: rgba(239, 68, 68, 0.1)
 * - Border: 1dp, rgba(239, 68, 68, 0.3)
 * - Text: #0F3D2E
 * - Action: #2BB15D button
 */
@Composable
fun ErrorState(
    title: String,
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon with tint
        Icon(
            imageVector = Icons.Default.PhoneDisabled,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color(0xFFC42855) // Error with green tint
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = PrimaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )
        
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(CornerRadius.small)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
