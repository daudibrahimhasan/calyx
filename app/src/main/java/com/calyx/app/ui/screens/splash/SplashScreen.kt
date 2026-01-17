package com.calyx.app.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calyx.app.R
import com.calyx.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Splash screen with animated logo and green gradient background.
 * 
 * Design Spec:
 * - Background: Linear gradient (#2BB15D → #4EC651)
 * - Logo: Calyx logo from resources
 * - App name: "Calyx" in Lufga Bold, white
 * - Tagline: "Call Log Analyzer" in Lufga Medium, white 80%
 * 
 * Animations:
 * - Logo scale: 0.8 → 1.0 with bounce
 * - Fade in: 0 → 1 over 800ms
 * - Duration: 1500ms before navigation
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Alpha animation
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )
    
    // Scale animation with bounce
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    // Subtle floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1500)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        ForestGreen,
                        VibrantGreen,
                        LimeAccent.copy(alpha = 0.8f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative gradient orbs
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-200).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            LimeAccent.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 120.dp, y = 250.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DeepGreen.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim)
                .scale(scaleAnim)
                .graphicsLayer {
                    translationY = floatOffset
                }
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.calyx_logo),
                contentDescription = "Calyx Logo",
                modifier = Modifier.size(140.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App name
            Text(
                text = "Calyx",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tagline
            Text(
                text = "Call Log Analyzer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
