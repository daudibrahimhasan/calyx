package com.calyx.app.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors
val Purple40 = Color(0xFF6750A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

// Material 3 Colors
val PrimaryLight = Color(0xFF6750A4)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFEADDFF)
val OnPrimaryContainerLight = Color(0xFF21005D)

val SecondaryLight = Color(0xFF625B71)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFE8DEF8)
val OnSecondaryContainerLight = Color(0xFF1D192B)

val TertiaryLight = Color(0xFF7D5260)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFD8E4)
val OnTertiaryContainerLight = Color(0xFF31111D)

val BackgroundLight = Color(0xFFFFFBFE)
val OnBackgroundLight = Color(0xFF1C1B1F)
val SurfaceLight = Color(0xFFFFFBFE)
val OnSurfaceLight = Color(0xFF1C1B1F)
val SurfaceVariantLight = Color(0xFFE7E0EC)
val OnSurfaceVariantLight = Color(0xFF49454F)

val OutlineLight = Color(0xFF79747E)
val OutlineVariantLight = Color(0xFFCAC4D0)

// Dark Theme Colors
val PrimaryDark = Color(0xFFD0BCFF)
val OnPrimaryDark = Color(0xFF381E72)
val PrimaryContainerDark = Color(0xFF4F378B)
val OnPrimaryContainerDark = Color(0xFFEADDFF)

val SecondaryDark = Color(0xFFCCC2DC)
val OnSecondaryDark = Color(0xFF332D41)
val SecondaryContainerDark = Color(0xFF4A4458)
val OnSecondaryContainerDark = Color(0xFFE8DEF8)

val TertiaryDark = Color(0xFFEFB8C8)
val OnTertiaryDark = Color(0xFF492532)
val TertiaryContainerDark = Color(0xFF633B48)
val OnTertiaryContainerDark = Color(0xFFFFD8E4)

val BackgroundDark = Color(0xFF1C1B1F)
val OnBackgroundDark = Color(0xFFE6E1E5)
val SurfaceDark = Color(0xFF1C1B1F)
val OnSurfaceDark = Color(0xFFE6E1E5)
val SurfaceVariantDark = Color(0xFF49454F)
val OnSurfaceVariantDark = Color(0xFFCAC4D0)

val OutlineDark = Color(0xFF938F99)
val OutlineVariantDark = Color(0xFF49454F)

// Accent Colors for Rankings
val Gold = Color(0xFFFFD700)
val GoldLight = Color(0xFFFFF8DC)
val GoldDark = Color(0xFFDAA520)
val OnGold = Color(0xFF3D3200)

val Silver = Color(0xFFC0C0C0)
val SilverLight = Color(0xFFE8E8E8)
val SilverDark = Color(0xFF808080)
val OnSilver = Color(0xFF2B2B2B)

val Bronze = Color(0xFFCD7F32)
val BronzeLight = Color(0xFFE6B87C)
val BronzeDark = Color(0xFF8B5A2B)
val OnBronze = Color(0xFF2E1A0A)

val RosePink = Color(0xFFF48FB1)
val RosePinkLight = Color(0xFFFFC1E3)
val RosePinkDark = Color(0xFFBF5F82)

// Gradient Colors
val GradientPurpleStart = Color(0xFF6750A4)
val GradientPurpleEnd = Color(0xFFEADDFF)

val GradientGoldStart = Color(0xFFFFD700)
val GradientGoldEnd = Color(0xFFFFF8DC)

val GradientSilverStart = Color(0xFFC0C0C0)
val GradientSilverEnd = Color(0xFFE8E8E8)

val GradientBronzeStart = Color(0xFFCD7F32)
val GradientBronzeEnd = Color(0xFFE6B87C)

// Status Colors
val ErrorLight = Color(0xFFB3261E)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFF9DEDC)
val OnErrorContainerLight = Color(0xFF410E0B)

// Avatar Background Colors (for initials)
val AvatarColors = listOf(
    Color(0xFF6750A4), // Purple
    Color(0xFF0077B6), // Blue
    Color(0xFF00A896), // Teal
    Color(0xFF7CB518), // Green
    Color(0xFFFF6B6B), // Coral
    Color(0xFFF77F00), // Orange
    Color(0xFFE63946), // Red
    Color(0xFF9B59B6), // Violet
    Color(0xFF3498DB), // Light Blue
    Color(0xFF1ABC9C), // Turquoise
)

/**
 * Get a consistent color for a name (same name always gets same color).
 */
fun getAvatarColor(name: String): Color {
    val hash = name.hashCode().let { if (it < 0) -it else it }
    return AvatarColors[hash % AvatarColors.size]
}
