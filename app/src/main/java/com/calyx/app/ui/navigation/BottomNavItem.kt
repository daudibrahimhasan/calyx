package com.calyx.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation destinations for the bottom navigation bar.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents
    )
    
    object Stats : BottomNavItem(
        route = "stats",
        title = "Stats",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )
    
    object People : BottomNavItem(
        route = "people",
        title = "People",
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People
    )
    
    object Settings : BottomNavItem(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
    
    companion object {
        val items = listOf(Home, Stats, People, Settings)
    }
}
