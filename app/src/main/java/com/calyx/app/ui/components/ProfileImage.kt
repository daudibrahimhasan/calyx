package com.calyx.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.calyx.app.ui.theme.*

/**
 * Profile image component with green gradient background and initials fallback.
 * 
 * Design Spec - Avatar States:
 * Default: Circular, gradient background
 * - Gradient based on hash of name
 * - Options:
 *   1. #8BD852 → #4EC651 (Lime to Vibrant)
 *   2. #4EC651 → #2BB15D (Vibrant to Forest)
 *   3. #2BB15D → #1C9C70 (Forest to Deep)
 *   4. #1C9C70 → #148189 (Deep to Teal)
 * - Initials: Lufga Bold, 18sp, White
 * - Border: 2dp solid rgba(255, 255, 255, 0.6)
 * - Text shadow: 0 1dp 3dp rgba(0, 0, 0, 0.2)
 * 
 * Photo loaded:
 * - Border: 2dp solid rgba(76, 198, 81, 0.5)
 */
@Composable
fun ProfileImage(
    photoUri: String?,
    displayName: String,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val initials = getInitials(displayName)
    val avatarGradient = getAvatarGradient(displayName)
    
    val gradient = Brush.linearGradient(
        colors = listOf(avatarGradient.start, avatarGradient.end)
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(gradient)
            .then(
                if (photoUri == null) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile photo of $displayName",
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = Color(0x804EC651), // rgba(76, 198, 81, 0.5)
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop,
                onError = {
                    // Will show initials as fallback
                }
            )
        }
        
        // Show initials when no photo
        if (photoUri == null) {
            Text(
                text = initials,
                color = Color.White,
                fontSize = (size.value / 2.5).sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/**
 * Profile image with rank badge overlay.
 */
@Composable
fun ProfileImageWithBadge(
    photoUri: String?,
    displayName: String,
    rank: Int,
    size: Dp = 48.dp,
    badgeSize: Dp = 20.dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        ProfileImage(
            photoUri = photoUri,
            displayName = displayName,
            size = size
        )
        
        // Badge overlay at bottom-right
        if (rank in 1..3) {
            RankBadge(
                rank = rank,
                size = badgeSize,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

/**
 * Extract initials from a display name.
 */
private fun getInitials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts.first().firstOrNull()?.uppercaseChar() ?: ""}${parts.last().firstOrNull()?.uppercaseChar() ?: ""}"
        parts.isNotEmpty() -> parts.first().take(2).uppercase()
        else -> "?"
    }
}
