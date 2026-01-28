package com.calyx.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.calyx.app.ui.theme.CalyzGradients
import com.calyx.app.ui.theme.PrimaryText

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalyzGradients.screenBackgroundGradient),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Custom pulsing dots animation (avoids broken keyframes API)
        PulsingDotsLoader()
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Syncing your call logs...",
            style = MaterialTheme.typography.bodyLarge,
            color = PrimaryText
        )
    }
}

@Composable
private fun PulsingDotsLoader() {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")
    
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600,
                        delayMillis = index * 200,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .scale(scale)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
            
            if (index < 2) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
