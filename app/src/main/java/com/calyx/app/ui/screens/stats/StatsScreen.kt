package com.calyx.app.ui.screens.stats

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.data.repository.CallSummary
import com.calyx.app.ui.theme.*
import kotlin.math.abs
import kotlin.random.Random

/**
 * Stats Screen - Personal analytics and insights.
 * 
 * Components:
 * 1. Activity Heatmap (7x5 grid for last 35 days)
 * 2. Trend Line (Bezier curve chart)
 * 3. "You vs. Average" comparison bar
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
            text = "Insights",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Your personal call analytics",
            fontSize = 14.sp,
            color = SecondaryText
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Activity Heatmap
        ActivityHeatmapCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Trend Line Chart
        TrendLineCard(summary = summary)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // You vs Average
        YouVsAverageCard(yourCalls = summary.totalCalls)
        
        Spacer(modifier = Modifier.height(80.dp) ) // Space for bottom nav
    }
}

/**
 * Activity Heatmap - 7x5 grid showing last 35 days of activity.
 * Colors range from Mint Cream (0 calls) to Deep Green (high activity).
 */
@Composable
private fun ActivityHeatmapCard(
    modifier: Modifier = Modifier
) {
    // Generate mock data for 35 days (would come from repository in real app)
    val activityData = remember {
        List(35) { Random.nextInt(0, 20) }
    }
    
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activity Heatmap",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                
                Text(
                    text = "Last 35 days",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Day labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 10.sp,
                        color = SecondaryText,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 5 weeks x 7 days grid
            for (week in 0 until 5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (day in 0 until 7) {
                        val index = week * 7 + day
                        val value = activityData.getOrElse(index) { 0 }
                        
                        HeatmapCell(
                            value = value,
                            maxValue = 20
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Less",
                    fontSize = 10.sp,
                    color = SecondaryText
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { intensity ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(getHeatmapColor(intensity))
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "More",
                    fontSize = 10.sp,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
private fun HeatmapCell(
    value: Int,
    maxValue: Int
) {
    val intensity = (value.toFloat() / maxValue).coerceIn(0f, 1f)
    
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(getHeatmapColor(intensity))
            .border(
                width = 0.5.dp,
                color = VibrantGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0) {
            Text(
                text = "$value",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = if (intensity > 0.5f) Color.White else PrimaryText.copy(alpha = 0.7f)
            )
        }
    }
}

private fun getHeatmapColor(intensity: Float): Color {
    return when {
        intensity <= 0f -> MintCream
        intensity < 0.25f -> SoftGreen
        intensity < 0.5f -> FreshGreen
        intensity < 0.75f -> VibrantGreen
        else -> DeepGreen
    }
}

/**
 * Trend Line Chart - Smooth Bezier curve showing weekly activity.
 */
@Composable
private fun TrendLineCard(
    summary: CallSummary,
    modifier: Modifier = Modifier
) {
    // Mock data for this week and last week
    val thisWeekData = remember {
        listOf(12, 8, 15, 10, 18, 22, 14)
    }
    val lastWeekData = remember {
        listOf(10, 12, 9, 14, 11, 16, 13)
    }
    
    val thisWeekTotal = thisWeekData.sum()
    val lastWeekTotal = lastWeekData.sum()
    val percentChange = if (lastWeekTotal > 0) {
        ((thisWeekTotal - lastWeekTotal).toFloat() / lastWeekTotal * 100).toInt()
    } else 0
    
    val isUp = percentChange >= 0
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Trend",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isUp) VibrantGreen else Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${abs(percentChange)}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isUp) VibrantGreen else Color(0xFFEF4444)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bezier curve chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                BezierLineChart(
                    data = thisWeekData,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Day labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 10.sp,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun BezierLineChart(
    data: List<Int>,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress"
    )
    
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        
        val maxValue = (data.maxOrNull() ?: 1).toFloat()
        val minValue = (data.minOrNull() ?: 0).toFloat()
        val range = (maxValue - minValue).coerceAtLeast(1f)
        
        val stepX = size.width / (data.size - 1)
        val padding = 20f
        
        val points = data.mapIndexed { index, value ->
            val x = index * stepX
            val y = size.height - padding - ((value - minValue) / range * (size.height - padding * 2))
            Offset(x, y * animatedProgress + (size.height / 2) * (1 - animatedProgress))
        }
        
        // Draw gradient fill
        val fillPath = Path().apply {
            moveTo(0f, size.height)
            points.forEachIndexed { index, point ->
                if (index == 0) {
                    lineTo(point.x, point.y)
                } else {
                    val prevPoint = points[index - 1]
                    val controlX1 = prevPoint.x + stepX / 2
                    val controlX2 = point.x - stepX / 2
                    cubicTo(controlX1, prevPoint.y, controlX2, point.y, point.x, point.y)
                }
            }
            lineTo(size.width, size.height)
            close()
        }
        
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    VibrantGreen.copy(alpha = 0.3f),
                    VibrantGreen.copy(alpha = 0.05f)
                )
            )
        )
        
        // Draw line
        val linePath = Path().apply {
            points.forEachIndexed { index, point ->
                if (index == 0) {
                    moveTo(point.x, point.y)
                } else {
                    val prevPoint = points[index - 1]
                    val controlX1 = prevPoint.x + stepX / 2
                    val controlX2 = point.x - stepX / 2
                    cubicTo(controlX1, prevPoint.y, controlX2, point.y, point.x, point.y)
                }
            }
        }
        
        drawPath(
            path = linePath,
            color = VibrantGreen,
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )
        
        // Draw dots
        points.forEach { point ->
            drawCircle(
                color = Color.White,
                radius = 6f,
                center = point
            )
            drawCircle(
                color = VibrantGreen,
                radius = 4f,
                center = point
            )
        }
    }
}

/**
 * You vs Average comparison bar.
 */
@Composable
private fun YouVsAverageCard(
    yourCalls: Int,
    modifier: Modifier = Modifier
) {
    // Mock global average
    val globalAverage = 85
    val maxValue = maxOf(yourCalls, globalAverage, 100)
    
    // Calculate percentile
    val percentile = when {
        yourCalls > globalAverage * 1.5 -> 15
        yourCalls > globalAverage * 1.2 -> 25
        yourCalls > globalAverage -> 40
        yourCalls > globalAverage * 0.8 -> 60
        else -> 75
    }
    
    val yourProgress by animateFloatAsState(
        targetValue = yourCalls.toFloat() / maxValue,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "yourProgress"
    )
    
    val avgProgress by animateFloatAsState(
        targetValue = globalAverage.toFloat() / maxValue,
        animationSpec = tween(800, delayMillis = 200, easing = EaseOutCubic),
        label = "avgProgress"
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
                text = "You vs. Average",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Your calls bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "You",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryText
                    )
                    Text(
                        text = "$yourCalls calls",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantGreen
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SoftGreen.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(yourProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(VibrantGreen, LimeAccent)
                                )
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Global average bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Global Average",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryText
                    )
                    Text(
                        text = "$globalAverage calls",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryText
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SoftGreen.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(avgProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(SecondaryText.copy(alpha = 0.4f))
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Percentile text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LimeAccent.copy(alpha = 0.15f))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏆 You are in the top $percentile% of talkers!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = DeepGreen
                )
            }
        }
    }
}
