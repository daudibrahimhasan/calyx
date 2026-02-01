package com.calyx.app.data.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Repository to handle checking for app updates from GitHub.
 */
class UpdateRepository(private val context: Context) {
    
    companion object {
        private const val TAG = "UpdateRepository"
        // Replace with your actual GitHub repository details
        private const val UPDATES_URL = "https://raw.githubusercontent.com/daudibrahimhasan/calyx/main/version.json"
        
        // Structure of version.json:
        // {
        //   "versionCode": 2,
        //   "versionName": "1.1",
        //   "updateUrl": "https://github.com/daudibrahimhasan/calyx/releases/latest",
        //   "releaseNotes": "Bug fixes and performance improvements"
        // }
    }

    data class UpdateInfo(
        val versionCode: Int,
        val versionName: String,
        val updateUrl: String,
        val releaseNotes: String,
        val isUpdateAvailable: Boolean
    )

    /**
     * Checks if a new update is available.
     * Returns UpdateInfo if successful, null otherwise.
     */
    suspend fun checkForUpdate(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL(UPDATES_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                
                val latestVersionCode = json.getInt("versionCode")
                val latestVersionName = json.getString("versionName")
                val updateUrl = json.getString("updateUrl")
                val releaseNotes = json.optString("releaseNotes", "")
                
                // Get current version code
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                val currentVersionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode.toInt()
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode
                }
                
                Log.d(TAG, "Current Version: $currentVersionCode, Latest Version: $latestVersionCode")
                
                return@withContext UpdateInfo(
                    versionCode = latestVersionCode,
                    versionName = latestVersionName,
                    updateUrl = updateUrl,
                    releaseNotes = releaseNotes,
                    isUpdateAvailable = latestVersionCode > currentVersionCode
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking for updates", e)
        }
        return@withContext null
    }
}
