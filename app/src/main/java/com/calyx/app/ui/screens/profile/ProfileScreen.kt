package com.calyx.app.ui.screens.profile

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.calyx.app.R
import com.calyx.app.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// DataStore for ghost mode preference only
private val Context.settingsDataStore by preferencesDataStore(name = "calyz_user_settings")

object SettingsPreferenceKeys {
    val GHOST_MODE = booleanPreferencesKey("ghost_mode")
}

/**
 * Profile Screen - User management and preferences.
 * 
 * Components:
 * 1. Profile Header (Avatar, Name, Email)
 * 2. Settings List (Ghost Mode, Theme, Clear Data)
 */
@Composable
fun ProfileScreen(
    onClearData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Haptics
    val haptic = LocalHapticFeedback.current
    
    // Theme manager for app-wide theme changes
    val themeManager = rememberThemeManager()
    val darkTheme by collectThemeState(themeManager)
    
    // Ghost mode state from DataStore
    var ghostMode by remember { mutableStateOf(false) }
    
    // Load ghost mode preference
    LaunchedEffect(Unit) {
        ghostMode = context.settingsDataStore.data.map { prefs ->
            prefs[SettingsPreferenceKeys.GHOST_MODE] ?: false
        }.first()
    }
    
    // Clear data confirmation dialog
    var showClearDialog by remember { mutableStateOf(false) }
    
    if (showClearDialog) {
        ClearDataDialog(
            onDismiss = { showClearDialog = false },
            onConfirm = {
                showClearDialog = false
                onClearData()
            }
        )
    }
    
    // Load user name from SharedPreferences
    val prefs = remember { context.getSharedPreferences("calyz_prefs", android.content.Context.MODE_PRIVATE) }
    val userName = remember { prefs.getString("user_name", "Calyz User") ?: "Calyz User" }
    
    // Choose background gradient based on theme
    val backgroundGradient = if (darkTheme) {
        CalyzGradients.darkScreenBackgroundGradient
    } else {
        CalyzGradients.screenBackgroundGradient
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        ProfileHeader(
            userName = if (userName.isBlank()) "Calyz User" else userName,
            isDarkTheme = darkTheme
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Settings Section
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            // Theme-aware colors
            val sectionLabelColor = if (darkTheme) MintHighlight else VibrantGreen
            val cardBgColor = if (darkTheme) MossGreen.copy(alpha = 0.4f) else Color.White
            val dividerColor = if (darkTheme) SageGreen.copy(alpha = 0.3f) else SoftGreen.copy(alpha = 0.3f)
            val textColor = if (darkTheme) BrightMint else PrimaryText
            val secondaryTextColor = if (darkTheme) MutedSage else SecondaryText
            
            Text(
                text = "Settings",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = sectionLabelColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (darkTheme) MintHighlight.copy(alpha = 0.2f) else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor)
            ) {
                Column {
                    // Ghost Mode Toggle
                    SettingsToggleItem(
                        icon = Icons.Outlined.VisibilityOff,
                        title = "Ghost Mode",
                        subtitle = if (ghostMode) "You're hidden from the leaderboard" else "You're visible on the leaderboard",
                        isChecked = ghostMode,
                        isDarkTheme = darkTheme,
                        onCheckedChange = { checked ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            ghostMode = checked
                            scope.launch {
                                context.settingsDataStore.edit { prefs ->
                                    prefs[SettingsPreferenceKeys.GHOST_MODE] = checked
                                }
                            }
                        }
                    )
                    
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = dividerColor
                    )
                    
                    // Theme Toggle
                    ThemeToggleItem(
                        isDarkTheme = darkTheme,
                        onThemeChange = { isDark ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            // Use ThemeManager for app-wide theme change
                            scope.launch {
                                themeManager.setDarkTheme(isDark)
                            }
                        }
                    )
                    
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = dividerColor
                    )
                    
                    // Clear Data
                    ClearDataItem(
                        onClick = { showClearDialog = true },
                        isDarkTheme = darkTheme
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // About Section
            Text(
                text = "About",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = sectionLabelColor,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (darkTheme) MintHighlight.copy(alpha = 0.2f) else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor)
            ) {
                Column {
                    SettingsInfoItem(
                        icon = Icons.Outlined.Info,
                        title = "Version",
                        value = "1.0.0",
                        isDarkTheme = darkTheme
                    )
                    
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = dividerColor
                    )
                    
                    SettingsInfoItem(
                        icon = Icons.Outlined.Security,
                        title = "Privacy",
                        value = "Data stays on device",
                        isDarkTheme = darkTheme
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Made with 💚 for call analysis\nbuilt by @daudibrahimhasan\npowered by Nexasity AI",
                fontSize = 12.sp,
                color = if (darkTheme) MutedSage.copy(alpha = 0.6f) else SecondaryText.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    isDarkTheme: Boolean = false
) {
    val headerGradient = if (isDarkTheme) {
        CalyzGradients.darkHeaderGradient
    } else {
        Brush.verticalGradient(colors = listOf(ForestGreen, VibrantGreen))
    }
    
    val accentColor = if (isDarkTheme) MintHighlight else Color.White
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerGradient)
            .statusBarsPadding()
            .padding(top = 16.dp, bottom = 32.dp), // Reduced from 48dp to 16dp
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar with glassmorphism effect
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkTheme) MossGreen.copy(alpha = 0.5f) else Color.White
                    )
                    .border(
                        width = 4.dp,
                        color = accentColor.copy(alpha = if (isDarkTheme) 0.3f else 1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.calyx_logo),
                    contentDescription = "Profile",
                    modifier = Modifier.size(60.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Display Name
            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) BrightMint else Color.White,
                fontFamily = LufgaFontFamily
            )
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    isDarkTheme: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    val accentColor = if (isDarkTheme) MintHighlight else VibrantGreen
    val textColor = if (isDarkTheme) BrightMint else PrimaryText
    val secondaryTextColor = if (isDarkTheme) MutedSage else SecondaryText
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = accentColor
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = secondaryTextColor
            )
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = secondaryTextColor,
                uncheckedTrackColor = if (isDarkTheme) SageGreen.copy(alpha = 0.3f) else SoftGreen.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun ThemeToggleItem(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val accentColor = if (isDarkTheme) MintHighlight else VibrantGreen
    val textColor = if (isDarkTheme) BrightMint else PrimaryText
    val secondaryTextColor = if (isDarkTheme) MutedSage else SecondaryText
    
    val sunColor by animateColorAsState(
        targetValue = if (!isDarkTheme) LimeAccent else (if (isDarkTheme) MutedSage else SecondaryText).copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "sunColor"
    )
    
    val moonColor by animateColorAsState(
        targetValue = if (isDarkTheme) NeonGreen else (if (isDarkTheme) MutedSage else SecondaryText).copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "moonColor"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = accentColor
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Theme",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = if (isDarkTheme) "Deep Jungle" else "Fresh Mint",
                fontSize = 12.sp,
                color = secondaryTextColor
            )
        }
        
        // Glassmorphism Sun/Moon toggle
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isDarkTheme) MossGreen.copy(alpha = 0.4f) else SoftGreen.copy(alpha = 0.2f)
                )
                .border(
                    width = 1.dp,
                    color = if (isDarkTheme) MintHighlight.copy(alpha = 0.2f) else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sun (Light)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (!isDarkTheme) Color.White 
                        else if (isDarkTheme) ForestShadow.copy(alpha = 0.5f) 
                        else Color.Transparent
                    )
                    .clickable { onThemeChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LightMode,
                    contentDescription = "Light theme",
                    modifier = Modifier.size(18.dp),
                    tint = sunColor
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            // Moon (Dark)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkTheme) MintHighlight.copy(alpha = 0.3f)
                        else Color.Transparent
                    )
                    .clickable { onThemeChange(true) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DarkMode,
                    contentDescription = "Dark theme",
                    modifier = Modifier.size(18.dp),
                    tint = moonColor
                )
            }
        }
    }
}

@Composable
private fun ClearDataItem(
    onClick: () -> Unit,
    isDarkTheme: Boolean = false
) {
    val errorColor = Color(0xFFEF4444)
    val secondaryTextColor = if (isDarkTheme) MutedSage else SecondaryText
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(errorColor.copy(alpha = if (isDarkTheme) 0.2f else 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = errorColor
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Clear Data",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = errorColor
            )
            Text(
                text = "Reset all call analysis data",
                fontSize = 12.sp,
                color = secondaryTextColor
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = secondaryTextColor.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    value: String,
    isDarkTheme: Boolean = false
) {
    val accentColor = if (isDarkTheme) MintHighlight else VibrantGreen
    val textColor = if (isDarkTheme) BrightMint else PrimaryText
    val secondaryTextColor = if (isDarkTheme) MutedSage else SecondaryText
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = accentColor
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            color = secondaryTextColor
        )
    }
}

@Composable
private fun ClearDataDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = "Clear All Data?",
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
        },
        text = {
            Text(
                text = "This will reset all your call analysis data. This action cannot be undone.",
                color = SecondaryText
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFEF4444)
                )
            ) {
                Text(
                    text = "Clear Data",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = SecondaryText
                )
            ) {
                Text("Cancel")
            }
        }
    )
}
