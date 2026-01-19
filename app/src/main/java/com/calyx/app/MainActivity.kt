package com.calyx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.calyx.app.ui.navigation.BottomNavItem
import com.calyx.app.ui.navigation.CalyxBottomNavBar
import com.calyx.app.ui.screens.leaderboard.LeaderboardScreen
import com.calyx.app.ui.screens.leaderboard.LeaderboardViewModel
import com.calyx.app.ui.screens.permissions.PermissionScreen
import com.calyx.app.ui.screens.profile.ProfileScreen
import com.calyx.app.ui.screens.splash.SplashScreen
import com.calyx.app.ui.screens.stats.StatsScreen
import com.calyx.app.ui.theme.CalyxTheme
import com.calyx.app.ui.theme.collectThemeState
import com.calyx.app.ui.theme.rememberThemeManager

/**
 * Main entry point for the Calyx app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Get theme state from ThemeManager
            val themeManager = rememberThemeManager()
            val isDarkTheme by collectThemeState(themeManager)
            
            CalyxTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalyxApp(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { newTheme ->
                            // Theme change is handled in ProfileScreen
                        }
                    )
                }
            }
        }
    }
}

/**
 * Navigation routes for the app.
 */
object Routes {
    const val SPLASH = "splash"
    const val PERMISSIONS = "permissions"
    const val MAIN = "main"
}

/**
 * Main app composable with navigation.
 */
@Composable
fun CalyxApp(
    isDarkTheme: Boolean = false,
    onThemeChange: (Boolean) -> Unit = {}
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Routes.PERMISSIONS) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Permission Request Screen
        composable(Routes.PERMISSIONS) {
            PermissionScreen(
                onPermissionsGranted = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.PERMISSIONS) { inclusive = true }
                    }
                }
            )
        }

        // Main screen with bottom navigation
        composable(Routes.MAIN) {
            MainScreen(isDarkTheme = isDarkTheme)
        }
    }
}

/**
 * Main screen with bottom navigation bar.
 * 
 * Tabs:
 * - Home: Leaderboard
 * - Stats: Analytics & Insights
 * - Profile: Settings & Control
 */
@Composable
fun MainScreen(isDarkTheme: Boolean = false) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavItem.Home.route
    
    // Shared ViewModel for data across screens
    val leaderboardViewModel: LeaderboardViewModel = viewModel()
    val summary by leaderboardViewModel.summary.collectAsState()
    
    // Stats screen data
    val dailyCallCounts by leaderboardViewModel.dailyCallCounts.collectAsState()
    val thisWeekCalls by leaderboardViewModel.thisWeekCalls.collectAsState()
    val lastWeekCalls by leaderboardViewModel.lastWeekCalls.collectAsState()

    Scaffold(
        bottomBar = {
            CalyxBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { item ->
                    bottomNavController.navigate(item.route) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Home - Leaderboard
            composable(BottomNavItem.Home.route) {
                LeaderboardScreen(
                    viewModel = leaderboardViewModel,
                    isDarkTheme = isDarkTheme
                )
            }
            
            // Stats - Analytics & Insights (with REAL data)
            composable(BottomNavItem.Stats.route) {
                StatsScreen(
                    summary = summary,
                    dailyCallCounts = dailyCallCounts,
                    thisWeekCalls = thisWeekCalls,
                    lastWeekCalls = lastWeekCalls,
                    isDarkTheme = isDarkTheme
                )
            }
            
            // Profile - Settings & Control
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onClearData = {
                        leaderboardViewModel.refreshData()
                    }
                )
            }
        }
    }
}
