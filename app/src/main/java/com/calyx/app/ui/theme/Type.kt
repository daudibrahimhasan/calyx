package com.calyx.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.calyx.app.R

// ============================================
// LUFGA FONT FAMILY
// ============================================

val LufgaFontFamily = FontFamily(
    Font(R.font.lufga_thin, FontWeight.Thin, FontStyle.Normal),
    Font(R.font.lufga_thinitalic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.lufga_extralight, FontWeight.ExtraLight, FontStyle.Normal),
    Font(R.font.lufga_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.lufga_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.lufga_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.lufga_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.lufga_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.lufga_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.lufga_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.lufga_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.lufga_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.lufga_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.lufga_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.lufga_extrabold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(R.font.lufga_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.lufga_black, FontWeight.Black, FontStyle.Normal),
    Font(R.font.lufga_blackitalic, FontWeight.Black, FontStyle.Italic),
)

// ============================================
// TYPOGRAPHY SYSTEM
// Based on the design specification
// ============================================

val Typography = Typography(
    // Display - Large Numbers (48sp Bold)
    displayLarge = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.25).sp
    ),
    // Display Medium (45sp)
    displayMedium = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    // Display Small - Rank Numbers (36sp Black)
    displaySmall = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    // Headline Large - Title (32sp Bold)
    headlineLarge = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    // Headline Medium - App Title (28sp Bold)
    headlineMedium = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    // Headline Small - Counts (24sp SemiBold)
    headlineSmall = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // Title Large - Section Headers (20sp SemiBold)
    titleLarge = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Title Medium - Card Titles (18sp Medium)
    titleMedium = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    // Title Small - Podium Names (16sp Medium)
    titleSmall = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    // Body Large - Primary Text (16sp Regular)
    bodyLarge = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Body Medium - Secondary Text (14sp Regular)
    bodyMedium = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    // Body Small - Caption (12sp Light/Regular)
    bodySmall = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    // Label Large - Tab Labels (14sp Medium)
    labelLarge = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    // Label Medium - Secondary Labels (12sp Medium)
    labelMedium = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // Label Small - Smallest labels (11sp Medium)
    labelSmall = TextStyle(
        fontFamily = LufgaFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// ============================================
// CUSTOM TEXT STYLES FOR SPECIFIC USE CASES
// ============================================

// Large stat numbers (36sp Bold) - for stats display
val StatNumberStyle = TextStyle(
    fontFamily = LufgaFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = 0.sp
)

// Rank number style (28sp Black) - for list items
val RankNumberStyle = TextStyle(
    fontFamily = LufgaFontFamily,
    fontWeight = FontWeight.Black,
    fontSize = 28.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp
)

// Winner rank (72sp Black) - for #1 podium background
val WinnerRankStyle = TextStyle(
    fontFamily = LufgaFontFamily,
    fontWeight = FontWeight.Black,
    fontSize = 72.sp,
    lineHeight = 80.sp,
    letterSpacing = 0.sp
)

// Runner-up rank (60sp Black) - for #2, #3 podium background
val RunnerUpRankStyle = TextStyle(
    fontFamily = LufgaFontFamily,
    fontWeight = FontWeight.Black,
    fontSize = 60.sp,
    lineHeight = 68.sp,
    letterSpacing = 0.sp
)

// Podium count style (24sp Bold) - for call counts on podiums
val PodiumCountStyle = TextStyle(
    fontFamily = LufgaFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
)

// Podium name style (18sp SemiBold) - for names on winner podium
val WinnerNameStyle = TextStyle(
    fontFamily = LufgaFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.sp
)

// Runner-up name style (16sp Medium)
val RunnerUpNameStyle = TextStyle(
    fontFamily = LufgaFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp
)
