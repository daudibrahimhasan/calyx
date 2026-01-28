package com.calyx.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.data.models.TimeRange
import com.calyx.app.ui.theme.*

/**
 * Compact filter strip combining category and time range filters.
 * Height: ~44dp (32dp pills + padding)
 * 
 * Design:
 * - Left: Category toggle (Most Called / Most Talked)
 * - Right: Time toggle (Weekly / All Time)
 */
@Composable
fun CompactFilterStrip(
    selectedCategory: RankingCategory,
    selectedTimeRange: TimeRange,
    onCategoryChange: (RankingCategory) -> Unit,
    onTimeRangeChange: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CalyzGradients.headerGradient)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category toggle - takes more space
        CompactPillToggle(
            options = RankingCategory.entries.map { it.displayName },
            selectedIndex = RankingCategory.entries.indexOf(selectedCategory),
            onSelect = { onCategoryChange(RankingCategory.entries[it]) },
            modifier = Modifier.weight(1.2f)
        )
        
        // Time range toggle
        CompactPillToggle(
            options = TimeRange.entries.map { it.displayName },
            selectedIndex = TimeRange.entries.indexOf(selectedTimeRange),
            onSelect = { onTimeRangeChange(TimeRange.entries[it]) },
            modifier = Modifier.weight(0.8f)
        )
    }
}

/**
 * Compact segmented pill toggle.
 * Height: 32dp max
 */
@Composable
fun CompactPillToggle(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = selectedIndex == index
            
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) DeepGreen else Color.Transparent,
                animationSpec = tween(200),
                label = "bg"
            )
            
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                animationSpec = tween(200),
                label = "text"
            )
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(14.dp))
                    .background(backgroundColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onSelect(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = textColor,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * HUD Stats Strip - Thin horizontal bar showing key stats.
 * Height: 40dp max
 * 
 * Design:
 * - Semi-transparent background
 * - Left: Phone icon + total calls
 * - Right: User icon + total contacts
 * - No divider, whitespace separation
 */
@Composable
fun HudStatsStrip(
    totalCalls: Int,
    totalContacts: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(DeepGreen.copy(alpha = 0.15f))
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Calls stat
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = VibrantGreen
            )
            Text(
                text = formatNumber(totalCalls),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            Text(
                text = "Calls",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = SecondaryText
            )
        }
        
        // Contacts stat
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = LimeAccent
            )
            Text(
                text = formatNumber(totalContacts),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            Text(
                text = "Contacts",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = SecondaryText
            )
        }
    }
}

/**
 * Format large numbers with comma separators.
 */
private fun formatNumber(number: Int): String {
    return number.toString().reversed().chunked(3).joinToString(",").reversed()
}
