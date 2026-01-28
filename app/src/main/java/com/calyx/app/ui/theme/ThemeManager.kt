package com.calyx.app.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * ThemeManager - Handles theme preference storage and retrieval.
 * 
 * Uses DataStore to persist theme choice across app restarts.
 * The theme state is exposed as a Flow so it can be collected in Compose.
 */
private val Context.themeDataStore by preferencesDataStore(name = "calyz_theme_prefs")

object ThemePreferenceKeys {
    val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
}

class ThemeManager(private val context: Context) {
    
    /**
     * Flow of the current theme preference.
     * Emits true for dark theme, false for light theme.
     */
    val isDarkTheme: Flow<Boolean> = context.themeDataStore.data
        .map { preferences ->
            preferences[ThemePreferenceKeys.IS_DARK_THEME] ?: false
        }
    
    /**
     * Update the theme preference.
     */
    suspend fun setDarkTheme(isDark: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[ThemePreferenceKeys.IS_DARK_THEME] = isDark
        }
    }
}

/**
 * Composable function to remember and provide the ThemeManager.
 */
@Composable
fun rememberThemeManager(): ThemeManager {
    val context = LocalContext.current
    return remember { ThemeManager(context) }
}

/**
 * Composable function to collect the theme state.
 * Returns true if dark theme is enabled, false otherwise.
 */
@Composable
fun collectThemeState(themeManager: ThemeManager): State<Boolean> {
    val systemInDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    return themeManager.isDarkTheme.collectAsState(initial = systemInDarkTheme)
}
