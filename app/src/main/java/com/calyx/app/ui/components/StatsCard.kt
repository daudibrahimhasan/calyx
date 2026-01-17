package com.calyx.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.data.repository.CallSummary
import com.calyx.app.ui.theme.*
import com.calyx.app.utils.DurationFormatter
import kotlinx.coroutines.delay

/**
 * Enhanced Stats Card with glassmorphism effect.
 * 
 * Design Spec:
 * - Background: Glass effect with linear gradient
 * - Border: 1.5dp, rgba(139, 216, 82, 0.3)
 * - Backdrop blur: 25px
 * - Corner radius: 24dp
 * - Shadow: 0 8dp 24dp rgba(28, 156, 112, 0.12)
 */
@Composable
fun StatsCard(
    summary: CallSummary,
    category: RankingCategory,
    modifier: Modifier = Modifier
) {
    // Animated counter effect
    var animatedCalls by remember { mutableStateOf(0) }
    var animatedContacts by remember { mutableStateOf(0) }
    
    LaunchedEffect(summary) {
        val targetCalls = summary.totalCalls
        val targetContacts = summary.uniqueContacts
        val duration = 800
        val steps = 30
        val delayPerStep = duration / steps
        
        repeat(steps) { step ->
            delay(delayPerStep.toLong())
            val progress = (step + 1).toFloat() / steps
            animatedCalls = (targetCalls * progress).toInt()
            animatedContacts = (targetContacts * progress).toInt()
        }
        animatedCalls = targetCalls
        animatedContacts = targetContacts
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(
                elevation = Elevation.level3,
                shape = RoundedCornerShape(CornerRadius.xLarge),
                ambientColor = ShadowLevel2,
                spotColor = ShadowLevel3
            )
            .clip(RoundedCornerShape(CornerRadius.xLarge))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.15f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                color = Color(0x4D8BD852), // rgba(139, 216, 82, 0.3)
                shape = RoundedCornerShape(CornerRadius.xLarge)
            )
    ) {
        // Inner glow highlight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 50f
                    )
                )
        )
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.statsCard),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Section - Primary Metric
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = TealGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (category) {
                            RankingCategory.MOST_CALLED -> "$animatedCalls"
                            RankingCategory.MOST_TALKED -> DurationFormatter.formatShort(summary.totalDuration)
                        },
                        style = StatNumberStyle,
                        color = PrimaryText
                    )
                }
                Text(
                    text = when (category) {
                        RankingCategory.MOST_CALLED -> "Total Calls"
                        RankingCategory.MOST_TALKED -> "Total Talk Time"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }
            
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                VibrantGreen.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            // Right Section - Contacts
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = LimeAccent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$animatedContacts",
                        style = StatNumberStyle,
                        color = PrimaryText
                    )
                }
                Text(
                    text = "Contacts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }
        }
    }
}
