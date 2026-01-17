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

// DataStore for preferences
private val Context.dataStore by preferencesDataStore(name = "calyx_settings")

object PreferenceKeys {
    val GHOST_MODE = booleanPreferencesKey("ghost_mode")
    val DARK_THEME = booleanPreferencesKey("dark_theme")
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
    
    // State from DataStore
    var ghostMode by remember { mutableStateOf(false) }
    var darkTheme by remember { mutableStateOf(false) }
    
    // Load preferences
    LaunchedEffect(Unit) {
        ghostMode = context.dataStore.data.map { prefs ->
            prefs[PreferenceKeys.GHOST_MODE] ?: false
        }.first()
        
        darkTheme = context.dataStore.data.map { prefs ->
            prefs[PreferenceKeys.DARK_THEME] ?: false
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalyxGradients.screenBackgroundGradient)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        ProfileHeader()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Settings Section
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = VibrantGreen,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    // Ghost Mode Toggle
                    SettingsToggleItem(
                        icon = Icons.Outlined.VisibilityOff,
                        title = "Ghost Mode",
                        subtitle = if (ghostMode) "You're hidden from the leaderboard" else "You're visible on the leaderboard",
                        isChecked = ghostMode,
                        onCheckedChange = { checked ->
                            ghostMode = checked
                            scope.launch {
                                context.dataStore.edit { prefs ->
                                    prefs[PreferenceKeys.GHOST_MODE] = checked
                                }
                            }
                        }
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = SoftGreen.copy(alpha = 0.3f)
                    )
                    
                    // Theme Toggle
                    ThemeToggleItem(
                        isDarkTheme = darkTheme,
                        onThemeChange = { isDark ->
                            darkTheme = isDark
                            scope.launch {
                                context.dataStore.edit { prefs ->
                                    prefs[PreferenceKeys.DARK_THEME] = isDark
                                }
                            }
                        }
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = SoftGreen.copy(alpha = 0.3f)
                    )
                    
                    // Clear Data
                    ClearDataItem(
                        onClick = { showClearDialog = true }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // About Section
            Text(
                text = "About",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = VibrantGreen,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    SettingsInfoItem(
                        icon = Icons.Outlined.Info,
                        title = "Version",
                        value = "1.0.0"
                    )
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = SoftGreen.copy(alpha = 0.3f)
                    )
                    
                    SettingsInfoItem(
                        icon = Icons.Outlined.Security,
                        title = "Privacy",
                        value = "Data stays on device"
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
                text = "Made with 💚 for call analysis\nCalyx v1.0.0",
                fontSize = 12.sp,
                color = SecondaryText.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
    }
}

@Composable
private fun ProfileHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ForestGreen, VibrantGreen)
                )
            )
            .padding(top = 48.dp, bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, Color.White, CircleShape),
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
                text = "Calyx User",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Email
            Text(
                text = "user@calyx.app",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
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
    onCheckedChange: (Boolean) -> Unit
) {
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
                .background(VibrantGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = VibrantGreen
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryText
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VibrantGreen,
                uncheckedThumbColor = SecondaryText,
                uncheckedTrackColor = SoftGreen.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun ThemeToggleItem(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val sunColor by animateColorAsState(
        targetValue = if (!isDarkTheme) LimeAccent else SecondaryText.copy(alpha = 0.4f),
        animationSpec = tween(300),
        label = "sunColor"
    )
    
    val moonColor by animateColorAsState(
        targetValue = if (isDarkTheme) VibrantGreen else SecondaryText.copy(alpha = 0.4f),
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
                .background(VibrantGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = VibrantGreen
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Theme",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryText
            )
            Text(
                text = if (isDarkTheme) "Deep Jungle" else "Fresh Mint",
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
        
        // Sun/Moon toggle
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SoftGreen.copy(alpha = 0.2f))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sun (Light)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (!isDarkTheme) Color.White else Color.Transparent)
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
                    .background(if (isDarkTheme) DeepGreen else Color.Transparent)
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
    onClick: () -> Unit
) {
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
                .background(Color(0xFFEF4444).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFFEF4444)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Clear Data",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFEF4444)
            )
            Text(
                text = "Reset all call analysis data",
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = SecondaryText.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
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
                .background(VibrantGreen.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = VibrantGreen
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryText,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            color = SecondaryText
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
