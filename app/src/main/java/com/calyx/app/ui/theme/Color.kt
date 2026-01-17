package com.calyx.app.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================
// CALYX GREEN EDITION - Fresh Green Palette
// ============================================

// Primary Green Colors
val MintCream = Color(0xFFE8F8F0)        // Ultra light - backgrounds
val SoftGreen = Color(0xFFBCE8C7)        // Light accent
val FreshGreen = Color(0xFF89D885)       // Medium light
val VibrantGreen = Color(0xFF4EC651)     // Primary brand
val ForestGreen = Color(0xFF2BB15D)      // Secondary
val DeepGreen = Color(0xFF1C9C70)        // Dark accent
val TealGreen = Color(0xFF148189)        // Alternative accent
val LimeAccent = Color(0xFF8BD852)       // Highlight/pop color

// Background Colors
val BackgroundBase = Color(0xFFF0FDF4)   // Screen background
val BackgroundWhite = Color(0xFFFFFFFF)  // Pure white
val ScreenGradientTop = Color(0xFFF0FDF4)
val ScreenGradientMid = Color(0xFFE8F8F0)
val ScreenGradientBottom = Color(0xFFFFFFFF)

// Text Colors
val PrimaryText = Color(0xFF0F3D2E)      // Dark green-black
val SecondaryText = Color(0xFF1C6F54)    // Muted green
val LightText = Color(0xFFFFFFFF)        // On colored backgrounds
val DisabledText = Color(0x661C9C70)     // 40% Deep Green
val AccentText = Color(0xFF148189)       // Teal for emphasis

// Glass Effect Colors
val GlassOverlay = Color(0x33FFFFFF)     // rgba(255, 255, 255, 0.2)
val GlassBorder = Color(0x4D4EC651)      // rgba(76, 198, 81, 0.3)
val GlassShadow = Color(0x261C9C70)      // rgba(28, 156, 112, 0.15)
val GreenTintGlass = Color(0x1A8BD852)   // rgba(139, 216, 82, 0.1)

// Cards/Glass surfaces
val CardGlass = Color(0x264EC651)        // rgba(76, 198, 81, 0.15)

// Material 3 Color Scheme - Light Theme
val PrimaryLight = VibrantGreen
val OnPrimaryLight = Color.White
val PrimaryContainerLight = SoftGreen
val OnPrimaryContainerLight = PrimaryText

val SecondaryLight = ForestGreen
val OnSecondaryLight = Color.White
val SecondaryContainerLight = MintCream
val OnSecondaryContainerLight = PrimaryText

val TertiaryLight = TealGreen
val OnTertiaryLight = Color.White
val TertiaryContainerLight = Color(0xFFD0F0F5)
val OnTertiaryContainerLight = Color(0xFF002022)

val BackgroundLight = BackgroundBase
val OnBackgroundLight = PrimaryText
val SurfaceLight = BackgroundWhite
val OnSurfaceLight = PrimaryText
val SurfaceVariantLight = MintCream
val OnSurfaceVariantLight = SecondaryText

val OutlineLight = Color(0x404EC651)     // 25% Vibrant Green
val OutlineVariantLight = Color(0x334EC651)

// Error Colors
val ErrorLight = Color(0xFFB3261E)
val OnErrorLight = Color.White
val ErrorContainerLight = Color(0xFFF9DEDC)
val OnErrorContainerLight = Color(0xFF410E0B)

// Dark Theme Colors (Green-tinted dark mode)
val PrimaryDark = LimeAccent
val OnPrimaryDark = Color(0xFF0A3D00)
val PrimaryContainerDark = DeepGreen
val OnPrimaryContainerDark = MintCream

val SecondaryDark = SoftGreen
val OnSecondaryDark = Color(0xFF0A3020)
val SecondaryContainerDark = Color(0xFF1A5040)
val OnSecondaryContainerDark = MintCream

val TertiaryDark = Color(0xFF80D8F0)
val OnTertiaryDark = Color(0xFF00363E)
val TertiaryContainerDark = Color(0xFF004F5B)
val OnTertiaryContainerDark = Color(0xFFB0F0FF)

val BackgroundDark = Color(0xFF0A1F15)
val OnBackgroundDark = MintCream
val SurfaceDark = Color(0xFF0A1F15)
val OnSurfaceDark = MintCream
val SurfaceVariantDark = Color(0xFF1A3F30)
val OnSurfaceVariantDark = SoftGreen

val OutlineDark = Color(0xFF608070)
val OutlineVariantDark = Color(0xFF405050)

// ============================================
// PODIUM & RANK COLORS - Green Themed
// ============================================

// Winner/Gold - Using Lime Accent gradient
val WinnerGradientStart = LimeAccent
val WinnerGradientEnd = VibrantGreen
val OnWinner = Color.White

// Silver - Using Soft/Fresh Greens
val SilverGradientStart = SoftGreen
val SilverGradientEnd = FreshGreen
val OnSilver = PrimaryText

// Bronze - Using Forest/Deep Greens
val BronzeGradientStart = ForestGreen
val BronzeGradientEnd = DeepGreen
val OnBronze = Color.White

// Legacy colors for backward compatibility
val Gold = LimeAccent
val GoldLight = Color(0xFFB5E89A)
val GoldDark = VibrantGreen
val OnGold = PrimaryText

val Silver = FreshGreen
val SilverLight = SoftGreen
val SilverDark = ForestGreen

val Bronze = DeepGreen
val BronzeLight = ForestGreen
val BronzeDark = TealGreen

val RosePink = TealGreen
val RosePinkLight = Color(0xFF60C0C8)
val RosePinkDark = Color(0xFF0A6068)

// ============================================
// GRADIENT DEFINITIONS
// ============================================

// Header gradient
val HeaderGradientStart = ForestGreen
val HeaderGradientEnd = VibrantGreen

// ============================================
// AVATAR COLORS - Green Themed Gradients
// ============================================

data class AvatarGradient(val start: Color, val end: Color)

val AvatarGradients = listOf(
    AvatarGradient(LimeAccent, VibrantGreen),
    AvatarGradient(VibrantGreen, ForestGreen),
    AvatarGradient(ForestGreen, DeepGreen),
    AvatarGradient(DeepGreen, TealGreen),
)

val AvatarColors = listOf(
    LimeAccent,
    VibrantGreen,
    ForestGreen,
    DeepGreen,
    TealGreen,
    FreshGreen,
    Color(0xFF66BB6A), // Light Forest
    Color(0xFF26A69A), // Teal variant
    Color(0xFF81C784), // Soft Green variant
    Color(0xFF4DB6AC), // Aqua green
)

/**
 * Get a consistent color for a name (same name always gets same color).
 */
fun getAvatarColor(name: String): Color {
    val hash = name.hashCode().let { if (it < 0) -it else it }
    return AvatarColors[hash % AvatarColors.size]
}

/**
 * Get a consistent gradient for a name.
 */
fun getAvatarGradient(name: String): AvatarGradient {
    val hash = name.hashCode().let { if (it < 0) -it else it }
    return AvatarGradients[hash % AvatarGradients.size]
}

// ============================================
// ELEVATION SHADOWS
// ============================================
val ShadowLevel1 = Color(0x141C9C70)  // 0.08 opacity
val ShadowLevel2 = Color(0x1F1C9C70)  // 0.12 opacity
val ShadowLevel3 = Color(0x261C9C70)  // 0.15 opacity
val ShadowLevel4 = Color(0x2E4EC651)  // 0.18 opacity
val GlowWinner = Color(0x408BD852)    // 0.25 opacity
