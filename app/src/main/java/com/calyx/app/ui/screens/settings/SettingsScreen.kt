package com.calyx.app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.BuildConfig
import com.calyx.app.ui.theme.*

/**
 * Settings screen with app preferences and information.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalyxGradients.screenBackgroundGradient)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Customize your experience",
                fontSize = 14.sp,
                color = SecondaryText
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Preferences Section
        SettingsSection(title = "Preferences") {
            SettingsItem(
                icon = Icons.Outlined.Palette,
                title = "Theme",
                subtitle = "Green (Default)",
                onClick = { /* TODO: Theme selector */ }
            )
            
            SettingsItem(
                icon = Icons.Outlined.DateRange,
                title = "Default Time Range",
                subtitle = "All Time",
                onClick = { /* TODO: Time range selector */ }
            )
            
            SettingsItem(
                icon = Icons.Outlined.Notifications,
                title = "Notifications",
                subtitle = "Disabled",
                onClick = { /* TODO: Notification settings */ }
            )
        }
        
        // Privacy Section
        SettingsSection(title = "Privacy") {
            SettingsItem(
                icon = Icons.Outlined.VisibilityOff,
                title = "Hide Phone Numbers",
                subtitle = "Enabled - Numbers are masked",
                onClick = { /* TODO: Privacy toggle */ }
            )
            
            SettingsItem(
                icon = Icons.Outlined.Delete,
                title = "Clear App Data",
                subtitle = "Remove cached call analysis",
                onClick = { /* TODO: Clear data */ }
            )
        }
        
        // About Section
        SettingsSection(title = "About") {
            SettingsItem(
                icon = Icons.Outlined.Info,
                title = "App Version",
                subtitle = "1.0.0",
                showArrow = false,
                onClick = { }
            )
            
            SettingsItem(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = "Help & Support",
                subtitle = "Get help using Calyx",
                onClick = {
                    // Open support URL
                }
            )
            
            SettingsItem(
                icon = Icons.Outlined.Star,
                title = "Rate App",
                subtitle = "Share your feedback on Play Store",
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("market://details?id=${context.packageName}")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Play Store not available
                    }
                }
            )
            
            SettingsItem(
                icon = Icons.Outlined.Share,
                title = "Share App",
                subtitle = "Tell your friends about Calyx",
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Check out Calyx - the call log analyzer!")
                    }
                    context.startActivity(Intent.createChooser(intent, "Share via"))
                }
            )
        }
        
        // Developer Info
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Made with 💚 for call analysis",
                    fontSize = 12.sp,
                    color = SecondaryText.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Calyx v1.0.0",
                    fontSize = 11.sp,
                    color = VibrantGreen.copy(alpha = 0.5f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = VibrantGreen,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                content()
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showArrow: Boolean = true,
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
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
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
        
        if (showArrow) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = SecondaryText.copy(alpha = 0.5f)
            )
        }
    }
}
