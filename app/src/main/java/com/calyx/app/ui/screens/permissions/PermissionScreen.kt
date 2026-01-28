package com.calyx.app.ui.screens.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.calyx.app.ui.theme.*

@Composable
fun PermissionScreen(
    onPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    var permissionsGranted by remember { mutableStateOf(false) }

    val permissions = listOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionResults ->
        if (permissionResults.values.all { it }) {
            permissionsGranted = true
            onPermissionsGranted()
        }
    }

    fun checkPermissions() {
        if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
            permissionsGranted = true
            onPermissionsGranted()
        }
    }

    LaunchedEffect(Unit) {
        checkPermissions()
        if (!permissionsGranted) {
            launcher.launch(permissions.toTypedArray())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CalyzGradients.screenBackgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Top section - Icon with glass effect
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = ShadowLevel3,
                        spotColor = ShadowLevel2
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                SoftGreen,
                                VibrantGreen.copy(alpha = 0.4f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = ForestGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Contacts,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = DeepGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Calyz needs your permission",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "To create your personalized leaderboard",
                style = MaterialTheme.typography.bodyLarge,
                color = SecondaryText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            PermissionExplanationCard(
                icon = Icons.Default.Phone,
                title = "Call Log Access",
                description = "To analyze your calling patterns and identify your most frequent contacts"
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermissionExplanationCard(
                icon = Icons.Default.Contacts,
                title = "Contacts Access",
                description = "To show contact names and photos in your leaderboard"
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { launcher.launch(permissions.toTypedArray()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(CornerRadius.medium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Grant Permissions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TealGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your data never leaves your device",
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PermissionExplanationCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(CornerRadius.medium),
                ambientColor = ShadowLevel1,
                spotColor = ShadowLevel1
            )
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.6f),
                        Color.White.copy(alpha = 0.4f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0x404EC651),
                shape = RoundedCornerShape(CornerRadius.medium)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(LimeAccent.copy(alpha = 0.3f), VibrantGreen.copy(alpha = 0.2f))
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = VibrantGreen.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = ForestGreen
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }
        }
    }
}
