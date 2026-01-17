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
import com.calyx.app.ui.screens.people.PeopleScreen
import com.calyx.app.ui.screens.permissions.PermissionScreen
import com.calyx.app.ui.screens.settings.SettingsScreen
import com.calyx.app.ui.screens.splash.SplashScreen
import com.calyx.app.ui.screens.stats.StatsScreen
import com.calyx.app.ui.theme.CalyxTheme

/**
 * Main entry point for the Calyx app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalyxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalyxApp()
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
fun CalyxApp() {
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
            MainScreen()
        }
    }
}

/**
 * Main screen with bottom navigation bar.
 */
@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: BottomNavItem.Home.route
    
    // Shared ViewModel for data across screens
    val leaderboardViewModel: LeaderboardViewModel = viewModel()
    val uiState by leaderboardViewModel.uiState.collectAsState()
    val callerStats by leaderboardViewModel.callerStatsList.collectAsState()
    val summary by leaderboardViewModel.summary.collectAsState()

    Scaffold(
        bottomBar = {
            CalyxBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { item ->
                    bottomNavController.navigate(item.route) {
                        // Pop up to the start destination to avoid building up a large stack
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when navigating back
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
            composable(BottomNavItem.Home.route) {
                LeaderboardScreen(viewModel = leaderboardViewModel)
            }
            
            composable(BottomNavItem.Stats.route) {
                StatsScreen(summary = summary)
            }
            
            composable(BottomNavItem.People.route) {
                PeopleScreen(contacts = callerStats)
            }
            
            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
