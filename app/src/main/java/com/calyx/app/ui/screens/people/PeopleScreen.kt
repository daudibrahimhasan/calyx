package com.calyx.app.ui.screens.people

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calyx.app.data.models.CallerStats
import com.calyx.app.ui.components.ProfileImage
import com.calyx.app.ui.theme.*
import com.calyx.app.utils.DurationFormatter
import com.calyx.app.utils.PhoneNumberUtils

/**
 * People screen - searchable directory of all contacts with call history.
 */
@Composable
fun PeopleScreen(
    contacts: List<CallerStats>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) {
            contacts.sortedBy { it.displayName.lowercase() }
        } else {
            contacts.filter { 
                it.displayName.contains(searchQuery, ignoreCase = true) ||
                it.phoneNumber.contains(searchQuery)
            }.sortedBy { it.displayName.lowercase() }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalyxGradients.screenBackgroundGradient)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "People",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${contacts.size} contacts with call history",
                fontSize = 14.sp,
                color = SecondaryText
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onClear = { searchQuery = "" }
            )
        }
        
        // Contact List
        if (filteredContacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchQuery.isNotBlank()) "No contacts found" else "No call history yet",
                    fontSize = 16.sp,
                    color = SecondaryText
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp, 
                    end = 16.dp, 
                    bottom = 80.dp
                )
            ) {
                // Group by first letter
                val grouped = filteredContacts.groupBy { 
                    it.displayName.firstOrNull()?.uppercaseChar() ?: '#' 
                }
                
                grouped.forEach { (letter, contactsInGroup) ->
                    item {
                        Text(
                            text = letter.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantGreen,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(
                        items = contactsInGroup,
                        key = { it.phoneNumber }
                    ) { contact ->
                        ContactListItem(
                            contact = contact,
                            onClick = { /* TODO: Show contact details */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = VibrantGreen.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = SecondaryText
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search contacts...",
                        fontSize = 14.sp,
                        color = SecondaryText.copy(alpha = 0.6f)
                    )
                }
                
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = PrimaryText
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(VibrantGreen)
                )
            }
            
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        modifier = Modifier.size(16.dp),
                        tint = SecondaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactListItem(
    contact: CallerStats,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.5.dp, VibrantGreen.copy(alpha = 0.3f), CircleShape)
        ) {
            ProfileImage(
                photoUri = contact.profilePhotoUri,
                displayName = contact.displayName,
                size = 48.dp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = PhoneNumberUtils.getDisplayName(contact.displayName, contact.phoneNumber),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = PhoneNumberUtils.maskPhoneNumber(contact.phoneNumber),
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Call stats
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${contact.totalCalls} calls",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = VibrantGreen
            )
            Text(
                text = DurationFormatter.formatShort(contact.totalDuration),
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }
}
