package com.calyx.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.ui.theme.*

/**
 * Premium Loading State with pulsing glass effect and Lufga typography.
 */
@Composable
fun LoadingState(
    message: String = "Gathering insights from your call history...",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")
    
    // Scale pulse for the container
    val containerScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "containerScale"
    )

    // Rotation for a subtle background glow
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalyzGradients.screenBackgroundGradient),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(containerScale),
            contentAlignment = Alignment.Center
        ) {
            // Background glowing orb
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer { rotationZ = rotation }
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(LimeAccent.copy(alpha = 0.5f), VibrantGreen.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
            
            // Glass container
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = VibrantGreen,
                    strokeWidth = 3.dp,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = PrimaryText,
            fontFamily = LufgaFontFamily,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 48.dp)
        )
    }
}

/**
 * Premium Empty State with Minimalist Green Aesthetic.
 */
@Composable
fun EmptyState(
    title: String = "No Data Yet",
    message: String = "Your call analysis will appear here once you make some calls.",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalyzGradients.screenBackgroundGradient)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(VibrantGreen.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhoneDisabled,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = VibrantGreen
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LufgaFontFamily,
            color = PrimaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            fontSize = 15.sp,
            color = SecondaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

/**
 * Premium Error State with Action.
 */
@Composable
fun ErrorState(
    title: String = "System Timeout",
    message: String = "Something went wrong while processing data.",
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalyzGradients.screenBackgroundGradient)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFFFEBEE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFFD32F2F)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LufgaFontFamily,
            color = PrimaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            fontSize = 15.sp,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )
        
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(50.dp).padding(horizontal = 32.dp)
            ) {
                Text(
                    text = actionText,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
