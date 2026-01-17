package com.calyx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.calyx.app.ui.screens.leaderboard.LeaderboardScreen
import com.calyx.app.ui.screens.permissions.PermissionScreen
import com.calyx.app.ui.screens.splash.SplashScreen
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
    const val LEADERBOARD = "leaderboard"
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
                    navController.navigate(Routes.LEADERBOARD) {
                        popUpTo(Routes.PERMISSIONS) { inclusive = true }
                    }
                }
            )
        }

        // Main Leaderboard Screen
        composable(Routes.LEADERBOARD) {
            LeaderboardScreen()
        }
    }
}
