package com.calyx.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.calyx.app.ui.theme.getAvatarColor

/**
 * Profile image component with photo loading and initials fallback.
 */
@Composable
fun ProfileImage(
    photoUri: String?,
    displayName: String,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val initials = getInitials(displayName)
    val backgroundColor = getAvatarColor(displayName)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
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
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                onError = {
                    // Will show initials as fallback (handled by the Box background)
                }
            )
            // Show initials as fallback overlay (will be hidden when image loads)
        }
        
        // Always show initials (will be behind the image when loaded)
        if (photoUri == null) {
            Text(
                text = initials,
                color = Color.White,
                fontSize = (size.value / 2.5).sp,
                fontWeight = FontWeight.Medium,
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
