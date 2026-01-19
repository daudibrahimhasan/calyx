package com.calyx.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ============================================
// GLASSMORPHISM EFFECTS
// Based on the UI Design Specification
// ============================================

/**
 * Primary glass card effect.
 * CSS equivalent:
 * background: linear-gradient(135deg, rgba(255,255,255,0.25) 0%, rgba(255,255,255,0.15) 100%)
 * backdrop-filter: blur(20px) saturate(180%)
 * border: 1px solid rgba(76, 198, 81, 0.3)
 * box-shadow: 0 8px 32px rgba(28, 156, 112, 0.12)
 */
object GlassModifiers {
    
    val primaryGlassGradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.25f),
            Color.White.copy(alpha = 0.15f)
        )
    )
    
    val enhancedGlassGradient = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.30f),
            Color.White.copy(alpha = 0.15f)
        )
    )
    
    val listItemGlassGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.08f)
        )
    )
    
    @Composable
    fun Modifier.primaryGlass(
        cornerRadius: Dp = 24.dp,
        borderWidth: Dp = 1.dp
    ): Modifier = this
        .shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(cornerRadius),
            ambientColor = ShadowLevel2,
            spotColor = ShadowLevel2
        )
        .clip(RoundedCornerShape(cornerRadius))
        .background(primaryGlassGradient)
        .border(
            width = borderWidth,
            color = GlassBorder,
            shape = RoundedCornerShape(cornerRadius)
        )
    
    @Composable
    fun Modifier.enhancedGlass(
        cornerRadius: Dp = 24.dp,
        borderWidth: Dp = 1.5.dp
    ): Modifier = this
        .shadow(
            elevation = 12.dp,
            shape = RoundedCornerShape(cornerRadius),
            ambientColor = ShadowLevel3,
            spotColor = ShadowLevel4
        )
        .clip(RoundedCornerShape(cornerRadius))
        .background(enhancedGlassGradient)
        .border(
            width = borderWidth,
            color = Color(0x598BD852), // rgba(139, 216, 82, 0.35)
            shape = RoundedCornerShape(cornerRadius)
        )
    
    @Composable
    fun Modifier.listItemGlass(
        cornerRadius: Dp = 18.dp
    ): Modifier = this
        .shadow(
            elevation = 4.dp,
            shape = RoundedCornerShape(cornerRadius),
            ambientColor = ShadowLevel1,
            spotColor = ShadowLevel1
        )
        .clip(RoundedCornerShape(cornerRadius))
        .background(listItemGlassGradient)
        .border(
            width = 1.dp,
            color = Color(0x404EC651), // rgba(76, 198, 81, 0.25)
            shape = RoundedCornerShape(cornerRadius)
        )
}

// ============================================
// GRADIENT DEFINITIONS
// ============================================

object CalyxGradients {
    
    // Header gradient: linear-gradient(180deg, #2BB15D 0%, #4EC651 100%)
    val headerGradient = Brush.verticalGradient(
        colors = listOf(ForestGreen, VibrantGreen)
    )
    
    // Winner podium gradient: linear-gradient(145deg, #8BD852 0%, #4EC651 100%)
    val winnerPodiumGradient = Brush.linearGradient(
        colors = listOf(LimeAccent, VibrantGreen)
    )
    
    // Silver podium gradient: linear-gradient(145deg, #BCE8C7 0%, #89D885 100%)
    val silverPodiumGradient = Brush.linearGradient(
        colors = listOf(SoftGreen, FreshGreen)
    )
    
    // Bronze podium gradient: linear-gradient(145deg, #2BB15D 0%, #1C9C70 100%)
    val bronzePodiumGradient = Brush.linearGradient(
        colors = listOf(ForestGreen, DeepGreen)
    )
    
    // Screen background gradient - Light theme
    val screenBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            BackgroundBase,
            MintCream,
            BackgroundWhite
        )
    )
    
    // ============================================
    // DARK JUNGLE THEME GRADIENTS
    // ============================================
    
    // Dark theme header gradient: linear-gradient(180deg, #1C3830 0%, #2D5045 100%)
    val darkHeaderGradient = Brush.verticalGradient(
        colors = listOf(ForestShadow, MossGreen)
    )
    
    // Dark screen background gradient: linear-gradient(180deg, #0A1612 → #132520 → #1C3830)
    val darkScreenBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            DeepJungle,
            DarkForest,
            ForestShadow
        )
    )
    
    // Dark winner podium gradient: linear-gradient(145deg, #8BD852 0%, #6FD68A 100%)
    val darkWinnerPodiumGradient = Brush.linearGradient(
        colors = listOf(BrightLime, MintHighlight)
    )
    
    // Dark silver podium gradient: linear-gradient(145deg, #6FD68A 0%, #4A6B5C 100%)
    val darkSilverPodiumGradient = Brush.linearGradient(
        colors = listOf(MintHighlight, SageGreen)
    )
    
    // Dark bronze podium gradient: linear-gradient(145deg, #4A6B5C 0%, #2D5045 100%)
    val darkBronzePodiumGradient = Brush.linearGradient(
        colors = listOf(SageGreen, MossGreen)
    )
    
    // Dark glass card gradient
    val darkGlassGradient = Brush.linearGradient(
        colors = listOf(
            MossGreen.copy(alpha = 0.3f),
            ForestShadow.copy(alpha = 0.2f)
        )
    )
    
    // Alternative teal accent gradient
    val tealAccentGradient = Brush.linearGradient(
        colors = listOf(TealGreen, DeepGreen)
    )
    
    // Subtle podium container radial gradient effect
    val podiumContainerGradient = Brush.radialGradient(
        colors = listOf(
            Color(0x0D8BD852), // rgba(139, 216, 82, 0.05)
            Color.Transparent
        )
    )
    
    // Shimmer loading gradient
    val shimmerGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0x33BCE8C7), // rgba(188, 232, 199, 0.2)
            Color(0x4D8BD852), // rgba(139, 216, 82, 0.3)
            Color(0x33BCE8C7)
        )
    )
    
    // Stats card horizontal gradient
    val statsCardGradient = Brush.horizontalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.25f),
            Color.White.copy(alpha = 0.20f),
            Color.White.copy(alpha = 0.15f)
        )
    )
    
    // Dark stats card gradient
    val darkStatsCardGradient = Brush.horizontalGradient(
        colors = listOf(
            MossGreen.copy(alpha = 0.25f),
            ForestShadow.copy(alpha = 0.20f),
            DarkForest.copy(alpha = 0.15f)
        )
    )
}

// ============================================
// CORNER RADIUS CONSTANTS
// ============================================

object CornerRadius {
    val micro = 8.dp      // Small badges
    val small = 12.dp     // Tab buttons
    val medium = 16.dp    // List items
    val large = 20.dp     // Podiums
    val xLarge = 24.dp    // Stats card, major containers
    val round = 100.dp    // Pills, fully rounded
}

// ============================================
// ELEVATION/SHADOW LEVELS
// ============================================

object Elevation {
    val level1 = 2.dp   // Subtle
    val level2 = 4.dp   // Cards
    val level3 = 8.dp   // Raised
    val level4 = 12.dp  // Floating
}

// ============================================
// SPACING CONSTANTS
// ============================================

object Spacing {
    val baseUnit = 8.dp
    
    // Margins
    val screenEdge = 16.dp
    val sectionSpacing = 24.dp
    val cardSpacing = 8.dp
    val podiumContainer = 24.dp
    
    // Padding
    val glassCard = 16.dp
    val statsCard = 20.dp
    val buttonHorizontal = 12.dp
    val buttonVertical = 10.dp
    val listItem = 16.dp
}
