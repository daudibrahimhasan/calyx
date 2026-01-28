package com.calyx.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.ui.theme.*

/**
 * Custom bottom navigation bar with the green theme.
 * 
 * Design:
 * - Glass effect background
 * - Filled icons when active, outlined when inactive
 * - Subtle scale animation on selection
 * - Green accent for active item
 * - Theme-aware colors (light/dark mode)
 */
@Composable
fun CalyzBottomNavBar(
    currentRoute: String,
    onNavigate: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // Use MaterialTheme for automatic dark mode support
    val backgroundColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = 8.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem.items.forEach { item ->
                val isSelected = currentRoute == item.route
                
                BottomNavItemView(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animations
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) VibrantGreen else SecondaryText,
        animationSpec = tween(200),
        label = "iconColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) VibrantGreen else SecondaryText.copy(alpha = 0.7f),
        animationSpec = tween(200),
        label = "textColor"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) VibrantGreen.copy(alpha = 0.1f) else Color.Transparent,
        animationSpec = tween(200),
        label = "bgColor"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Using animated background color for feedback
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
            modifier = Modifier
                .size(24.dp)
                .scale(scale),
            tint = iconColor
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}
