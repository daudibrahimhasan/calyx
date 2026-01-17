package com.calyx.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.ui.theme.*

/**
 * Category tabs styled as a segmented pill control with glass effect.
 * 
 * Design Spec:
 * - Background: Glass effect with rgba(255, 255, 255, 0.25)
 * - Border: 1dp, rgba(76, 198, 81, 0.4)
 * - Backdrop blur: 15px
 * - Corner Radius: 12dp
 * - Active: Solid white background, #2BB15D text
 * - Inactive: Transparent background, White (85% opacity) text
 */
@Composable
fun CategoryTabs(
    selectedCategory: RankingCategory,
    onCategoryChange: (RankingCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = RankingCategory.entries
    val selectedIndex = categories.indexOf(selectedCategory)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenEdge, vertical = 12.dp)
    ) {
        // Glass container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(CornerRadius.small),
                    ambientColor = ShadowLevel1,
                    spotColor = ShadowLevel1
                )
                .clip(RoundedCornerShape(CornerRadius.small))
                .background(Color.White.copy(alpha = 0.25f))
                .border(
                    width = 1.dp,
                    color = Color(0x664EC651), // rgba(76, 198, 81, 0.4)
                    shape = RoundedCornerShape(CornerRadius.small)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categories.forEachIndexed { index, category ->
                val isSelected = selectedIndex == index
                
                SegmentedTabItem(
                    text = category.displayName,
                    isSelected = isSelected,
                    onClick = { onCategoryChange(category) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SegmentedTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        animationSpec = tween(durationMillis = 250),
        label = "bgColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) ForestGreen else Color.White.copy(alpha = 0.85f),
        animationSpec = tween(durationMillis = 250),
        label = "textColor"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = 250),
        label = "elevation"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .then(
                if (isSelected) {
                    Modifier.shadow(
                        elevation = elevation,
                        shape = RoundedCornerShape(CornerRadius.micro),
                        ambientColor = Color(0x4D2BB15D), // rgba(43, 177, 93, 0.3)
                        spotColor = Color(0x4D2BB15D)
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(CornerRadius.micro))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = textColor
        )
    }
}
