package com.calyx.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation destinations for the bottom navigation bar.
 * 
 * Tabs:
 * 1. Home - Leaderboard (Trophy icon)
 * 2. Stats - Analytics & Insights (Chart icon)
 * 3. Profile - Settings & Control (Person icon)
 */
data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    companion object {
        val Home = BottomNavItem(
            route = "home",
            title = "Home",
            selectedIcon = Icons.Filled.EmojiEvents,
            unselectedIcon = Icons.Outlined.EmojiEvents
        )
        
        val Stats = BottomNavItem(
            route = "stats",
            title = "Stats",
            selectedIcon = Icons.Filled.Insights,
            unselectedIcon = Icons.Outlined.Insights
        )
        
        val Profile = BottomNavItem(
            route = "profile",
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
        
        val items: List<BottomNavItem> = listOf(Home, Stats, Profile)
    }
}
