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
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Determine gradients and colors based on theme
    val screenGradient = if (isDarkTheme) CalyxGradients.darkScreenBackgroundGradient else CalyxGradients.screenBackgroundGradient
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val cardBgColor = MaterialTheme.colorScheme.surface

    // Calculate today's calls (last item in the list)
    val todayCalls = if (dailyCallCounts.isNotEmpty()) dailyCallCounts.last() else 0
    
    // Calculate this week's total calls
    val thisWeekTotal = thisWeekCalls.sum()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(screenGradient)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp) // Space for bottom nav
    ) {
        // Header
        Text(
            text = "Insights",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Your personal call analytics",
            fontSize = 14.sp,
            color = subTextColor
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Activity Heatmap - Using REAL data
        ActivityHeatmapCard(
            dailyCallCounts = dailyCallCounts,
            cardBgColor = cardBgColor, 
            textColor = textColor, 
            subTextColor = subTextColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Trend Line Chart - Using REAL data
        TrendLineCard(
            thisWeekData = thisWeekCalls,
            lastWeekData = lastWeekCalls,
            cardBgColor = cardBgColor, 
            textColor = textColor, 
            subTextColor = subTextColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // You vs Average - Today
        YouVsAverageCard(
            title = "You vs. Average - Today",
            yourCalls = todayCalls,
            averageCalls = 32, // Average daily calls
            periodLabel = "today",
            cardBgColor = cardBgColor, 
            textColor = textColor, 
            subTextColor = subTextColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // You vs Average - This Week
        YouVsAverageCard(
            title = "You vs. Average - This Week",
            yourCalls = thisWeekTotal,
            averageCalls = 210, // Average weekly calls
            periodLabel = "this week",
            cardBgColor = cardBgColor, 
            textColor = textColor, 
            subTextColor = subTextColor
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
                
                Text(
                    text = "Last 35 days",
                    fontSize = 12.sp,
                    color = subTextColor
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
            
            // Grid: 6 rows x 7 columns to ensure all 35 days fit even with offset
            // We need to offset by firstDayOfWeek to align correctly
            
            for (week in 0 until 6) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (dayOfWeek in 0 until 7) {
                        // Calculate if this cell should have data
                        // First row: only show cells starting from firstDayOfWeek
                        val cellIndex = week * 7 + dayOfWeek - firstDayOfWeek
                        
                        // Check if we have data for this index
                        if (cellIndex >= 0 && cellIndex < dailyCallCounts.size) {
                            val count = dailyCallCounts[cellIndex]
                            HeatmapCell(
                                value = count,
                                textColor = textColor
                            )
                        } else {
                            // Empty cell (before data starts or after data ends)
                            Box(modifier = Modifier.size(36.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Legend with consistent color scale
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "0",
                    fontSize = 10.sp,
                    color = subTextColor
                )
                
                Spacer(modifier = Modifier.width(6.dp))
                
                // Color scale legend: 0, 1-5, 6-10, 11-15, 16+
                listOf(0, 3, 8, 13, 20).forEach { sampleCount ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(getHeatmapColorByCount(sampleCount))
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                
                Spacer(modifier = Modifier.width(3.dp))
                
                Text(
                    text = "16+",
                    fontSize = 10.sp,
                    color = subTextColor
                )
            }
        }
    }
}

@Composable
private fun HeatmapCell(
    value: Int,
    textColor: Color
) {
    val bgColor = getHeatmapColorByCount(value)
    // Determine if text should be white (for darker cells)
    val isHighActivity = value >= 11
    
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(
                width = 0.5.dp,
                color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Always show the number (even 0 shows as blank cell)
        if (value > 0) {
            Text(
                text = "$value",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = if (isHighActivity) Color.White else textColor.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Get heatmap color based on ABSOLUTE call count (not percentage).
 * This ensures the SAME call count ALWAYS shows the SAME color.
 * 
 * Color Scale:
 * - 0 calls: Very light green (#E8F5E9)
 * - 1-5 calls: Light green (#A5D6A7)  
 * - 6-10 calls: Medium green (#66BB6A)
 * - 11-15 calls: Dark green (#43A047)
 * - 16+ calls: Darkest green (#2E7D32)
 */
private fun getHeatmapColorByCount(count: Int): Color {
    return when {
        count <= 0 -> Color(0xFFE8F5E9)  // Very light green
        count <= 5 -> Color(0xFFA5D6A7)   // Light green
        count <= 10 -> Color(0xFF66BB6A)  // Medium green
        count <= 15 -> Color(0xFF43A047)  // Dark green
        else -> Color(0xFF2E7D32)         // Darkest green
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
                        imageVector = if (isUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
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
