package com.calyx.app.ui.screens.stats

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneMissed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.data.repository.CallSummary
import com.calyx.app.ui.theme.*
import com.calyx.app.utils.DurationFormatter

/**
 * Stats screen showing charts and insights about call patterns.
 */
@Composable
fun StatsScreen(
    summary: CallSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalyxGradients.screenBackgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Statistics",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Your call patterns at a glance",
            fontSize = 14.sp,
            color = SecondaryText
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Call Distribution Pie Chart
        CallDistributionCard(summary = summary)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quick Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                title = "Incoming",
                value = "${summary.incomingCalls}",
                icon = Icons.Default.ArrowDownward,
                color = ForestGreen,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "Outgoing",
                value = "${summary.outgoingCalls}",
                icon = Icons.Default.ArrowUpward,
                color = VibrantGreen,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickStatCard(
                title = "Missed",
                value = "${summary.missedCalls}",
                icon = Icons.Default.PhoneMissed,
                color = TealGreen,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "Talk Time",
                value = DurationFormatter.formatShort(summary.totalDuration),
                icon = Icons.Default.Phone,
                color = LimeAccent,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Average Call Duration Card
        AverageCallCard(summary = summary)
        
        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
    }
}

@Composable
private fun CallDistributionCard(
    summary: CallSummary,
    modifier: Modifier = Modifier
) {
    val total = summary.incomingCalls + summary.outgoingCalls + summary.missedCalls
    val incomingPercent = if (total > 0) summary.incomingCalls.toFloat() / total else 0f
    val outgoingPercent = if (total > 0) summary.outgoingCalls.toFloat() / total else 0f
    val missedPercent = if (total > 0) summary.missedCalls.toFloat() / total else 0f
    
    // Animation
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Call Distribution",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pie Chart
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        val strokeWidth = 20.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val center = Offset(size.width / 2, size.height / 2)
                        
                        var startAngle = -90f
                        
                        // Incoming (Green)
                        val incomingSweep = incomingPercent * 360f * animatedProgress
                        drawArc(
                            color = ForestGreen,
                            startAngle = startAngle,
                            sweepAngle = incomingSweep,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += incomingSweep
                        
                        // Outgoing (Vibrant)
                        val outgoingSweep = outgoingPercent * 360f * animatedProgress
                        drawArc(
                            color = VibrantGreen,
                            startAngle = startAngle,
                            sweepAngle = outgoingSweep,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += outgoingSweep
                        
                        // Missed (Teal)
                        val missedSweep = missedPercent * 360f * animatedProgress
                        drawArc(
                            color = TealGreen,
                            startAngle = startAngle,
                            sweepAngle = missedSweep,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    
                    Text(
                        text = "$total",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // Legend
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LegendItem(
                        color = ForestGreen,
                        label = "Incoming",
                        value = summary.incomingCalls,
                        percent = (incomingPercent * 100).toInt()
                    )
                    LegendItem(
                        color = VibrantGreen,
                        label = "Outgoing",
                        value = summary.outgoingCalls,
                        percent = (outgoingPercent * 100).toInt()
                    )
                    LegendItem(
                        color = TealGreen,
                        label = "Missed",
                        value = summary.missedCalls,
                        percent = (missedPercent * 100).toInt()
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    value: Int,
    percent: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = SecondaryText
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$value ($percent%)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryText
        )
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = color
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
private fun AverageCallCard(
    summary: CallSummary,
    modifier: Modifier = Modifier
) {
    val avgDuration = if (summary.totalCalls > 0) {
        summary.totalDuration / summary.totalCalls
    } else 0L
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Average Call Duration",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DurationFormatter.formatDetailed(avgDuration),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantGreen
                )
                
                Text(
                    text = "per call",
                    fontSize = 14.sp,
                    color = SecondaryText
                )
            }
        }
    }
}
