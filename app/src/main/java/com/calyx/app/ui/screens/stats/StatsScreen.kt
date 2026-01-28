package com.calyx.app.ui.screens.stats

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

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
    dailyCallCounts: List<Int> = emptyList(), // Real heatmap data (List of 35 ints)
    thisWeekCalls: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0), // Real weekly data
    lastWeekCalls: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0), // Real last week data
    globalStats: com.calyx.app.data.models.GlobalStats = com.calyx.app.data.models.GlobalStats(),
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Determine gradients and colors based on theme
    val screenGradient = if (isDarkTheme) CalyzGradients.darkScreenBackgroundGradient else CalyzGradients.screenBackgroundGradient
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val cardBgColor = MaterialTheme.colorScheme.surface

    // Calculate today's calls (last item in the list)
    val todayCalls = if (dailyCallCounts.isNotEmpty()) dailyCallCounts.last() else 0
    
    // Calculate this week's total calls
    val thisWeekTotal = thisWeekCalls.sum()
    
    // Calculate Global Averages
    val avgDailyCalls = if (globalStats.today.active_users > 0) 
        (globalStats.today.calls / globalStats.today.active_users).toInt() 
    else 0 // Default fallback if no data
    
    val avgWeeklyCalls = if (globalStats.week.active_users > 0) 
        (globalStats.week.calls / globalStats.week.active_users).toInt() 
    else 0 // Default fallback

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(screenGradient)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp) // Space for bottom nav
    ) {
        // Header
        Text(
            text = "Insights",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Your personal call analytics",
            fontSize = 14.sp,
            color = subTextColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Activity Heatmap - Using REAL data
        ActivityHeatmapCard(
            dailyCallCounts = dailyCallCounts,
            cardBgColor = cardBgColor, 
            textColor = textColor, 
            subTextColor = subTextColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Trend Line Chart - Using REAL data
        TrendLineCard(
            thisWeekData = thisWeekCalls,
            lastWeekData = lastWeekCalls,
            cardBgColor = cardBgColor, 
            textColor = textColor, 
            subTextColor = subTextColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // You vs Average - Today
        YouVsAverageCard(
            title = "You vs. Average - Today",
            yourCalls = todayCalls,
            averageCalls = avgDailyCalls,
            periodLabel = "today",
            cardBgColor = cardBgColor, 
            textColor = textColor, 
            subTextColor = subTextColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // You vs Average - This Week
        YouVsAverageCard(
            title = "You vs. Average - This Week",
            yourCalls = thisWeekTotal,
            averageCalls = avgWeeklyCalls,
            periodLabel = "this week",
            cardBgColor = cardBgColor, 
            textColor = textColor, 
            subTextColor = subTextColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/**
 * Activity Heatmap - 7x5 grid showing last 35 days of activity.
 * Uses REAL call log data with consistent color coding.
 * 
 * Grid Layout:
 * - 7 columns (Mon-Sun)
 * - 5 rows (weeks)
 * - Cell 0 = 35 days ago, Cell 34 = today (last index of list)
 * - Days are properly aligned to their day of week
 */
@Composable
private fun ActivityHeatmapCard(
    dailyCallCounts: List<Int>,
    modifier: Modifier = Modifier,
    cardBgColor: Color,
    textColor: Color,
    subTextColor: Color
) {
    // Get the day of week for the first day (34 days ago) - for proper grid alignment
    val firstDayOfWeek = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -34)
        // Convert to 0=Monday, 6=Sunday
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        if (dow == Calendar.SUNDAY) 6 else dow - 2
    }
    
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor)
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
                    color = textColor
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Day labels (Mon-Sun)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 10.sp,
                        color = subTextColor,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Grid: 6 rows x 7 columns
            // Ensure we have at least 35 values (fill with 0 if needed)
            val paddedCounts = if (dailyCallCounts.size >= 35) {
                dailyCallCounts
            } else {
                dailyCallCounts + List(35 - dailyCallCounts.size) { 0 }
            }
            
            for (week in 0 until 6) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (dayOfWeek in 0 until 7) {
                        val cellIndex = week * 7 + dayOfWeek - firstDayOfWeek
                        
                        // Show cell if index is valid for the 35-day range
                        if (cellIndex >= 0 && cellIndex < 35) {
                            val count = paddedCounts.getOrElse(cellIndex) { 0 }
                            HeatmapCell(
                                value = count,
                                textColor = textColor
                            )
                        } else {
                            // Empty cell (off-grid, e.g., days before the 35-day range started)
                            Box(modifier = Modifier.size(36.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Simpler Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Less", fontSize = 10.sp, color = subTextColor)
                Spacer(modifier = Modifier.width(8.dp))
                
                // Color scale legend: 0, 5, 15, 25, 35, 45, 55 (Samples for each range)
                listOf(0, 5, 15, 25, 35, 45, 55).forEach { sampleCount ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(getHeatmapBrushByCount(sampleCount))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                
                Spacer(modifier = Modifier.width(4.dp))
                Text("More", fontSize = 10.sp, color = subTextColor)
            }
        }
    }
}

@Composable
private fun HeatmapCell(
    value: Int,
    textColor: Color
) {
    val bgBrush = remember(value) { getHeatmapBrushByCount(value) }
    
    // Special visual for 51+ calls ("God Mode" activity)
    val isSpecial = value >= 51
    val shape = remember { RoundedCornerShape(6.dp) } // Constant shape

    // Determine text color based on value
    val cellTextColor = remember(value) {
        when {
            value == 0 -> Color.White.copy(alpha = 0.3f)
            value <= 20 -> Color.Black.copy(alpha = 0.7f)
            else -> Color.White
        }
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(shape)
            .background(bgBrush)
            .border(
                width = if (value > 0) 0.dp else 1.dp,
                color = if (value > 0) Color.Transparent else Color.White.copy(alpha = 0.05f),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Special "Pulse" or "Glow" for 51+
        if (isSpecial) {
             Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent)
                        )
                    )
             )
        }

        if (value > 0) {
            Text(
                text = value.toString(),
                color = cellTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Enhanced Gradient Scale: Light to Deep (Low to High activity)
 * 0: Dark Grey (Empty)
 * 1-10: Pale Mint -> Soft Green (Light)
 * 11-20: Soft Green -> Fresh Green
 * 21-30: Fresh Green -> Vibrant Green
 * 31-40: Vibrant Green -> Rich Forest
 * 41-50: Rich Forest -> Deepest Jungle (Deep)
 * 51+: Deep Jungle -> Golden Olive (Legendary Deep)
 */
private fun getHeatmapBrushByCount(count: Int): Brush {
    return when {
        count <= 0 -> Brush.linearGradient(listOf(Color(0xFF1E1E1E), Color(0xFF1E1E1E))) // Dark Grey
        count <= 10 -> Brush.verticalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))) // Pale -> Soft
        count <= 20 -> Brush.verticalGradient(listOf(Color(0xFFC8E6C9), Color(0xFFA5D6A7))) // Soft -> Fresh
        count <= 30 -> Brush.verticalGradient(listOf(Color(0xFFA5D6A7), Color(0xFF66BB6A))) // Fresh -> Vibrant
        count <= 40 -> Brush.verticalGradient(listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))) // Vibrant -> Rich
        count <= 50 -> Brush.verticalGradient(listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))) // Rich -> Deep
        else -> Brush.verticalGradient(listOf(Color(0xFF1B5E20), Color(0xFFFFD700)))        // Deep -> Gold
    }
}

/**
 * Trend Line Chart - Smooth Bezier curve showing weekly activity.
 * Uses REAL call log data.
 */
@Composable
private fun TrendLineCard(
    thisWeekData: List<Int>,
    lastWeekData: List<Int>,
    modifier: Modifier = Modifier,
    cardBgColor: Color,
    textColor: Color,
    subTextColor: Color
) {
    val thisWeekTotal = thisWeekData.sum()
    val lastWeekTotal = lastWeekData.sum()
    val percentChange = if (lastWeekTotal > 0) {
        ((thisWeekTotal - lastWeekTotal).toFloat() / lastWeekTotal * 100).toInt()
    } else if (thisWeekTotal > 0) {
        100 // If last week was 0 and this week has calls, show 100% increase
    } else {
        0
    }
    
    val isUp = percentChange >= 0
    val trendColor = if (isUp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor)
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
                    color = textColor
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isUp) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = trendColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${abs(percentChange)}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = trendColor
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
                        color = subTextColor
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
    val primaryColor = MaterialTheme.colorScheme.primary
    
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
                    primaryColor.copy(alpha = 0.3f),
                    primaryColor.copy(alpha = 0.05f)
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
            color = primaryColor,
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
                color = primaryColor,
                radius = 4f,
                center = point
            )
        }
    }
}

/**
 * You vs Average comparison bar.
 */
/**
 * You vs Average comparison bar.
 */
@Composable
private fun YouVsAverageCard(
    title: String,
    yourCalls: Int,
    averageCalls: Int,
    periodLabel: String,
    modifier: Modifier = Modifier,
    cardBgColor: Color,
    textColor: Color,
    subTextColor: Color
) {
    val maxValue = maxOf(yourCalls, averageCalls, 10).toFloat()
    
    // Calculate comparison text
    val difference = yourCalls - averageCalls
    val comparisonText = when {
        difference > 0 -> "🏆 You are above average by $difference calls $periodLabel!"
        difference < 0 -> "You are below average by ${abs(difference)} calls $periodLabel."
        else -> "You are exactly on average $periodLabel!"
    }
    
    val yourProgress by animateFloatAsState(
        targetValue = yourCalls.toFloat() / maxValue,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "yourProgress"
    )
    
    val avgProgress by animateFloatAsState(
        targetValue = averageCalls.toFloat() / maxValue,
        animationSpec = tween(800, delayMillis = 200, easing = EaseOutCubic),
        label = "avgProgress"
    )
    
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
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
                        color = textColor
                    )
                    Text(
                        text = "$yourCalls calls",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(primaryColor.copy(alpha = 0.1f)) // Lighter background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(yourProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(primaryColor, LimeAccent)
                                )
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Average bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Average User",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                    Text(
                        text = "$averageCalls calls",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = subTextColor
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(primaryColor.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(avgProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(subTextColor.copy(alpha = 0.4f))
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Comparison text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LimeAccent.copy(alpha = 0.15f))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comparisonText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = DeepGreen
                )
            }
        }
    }
}
