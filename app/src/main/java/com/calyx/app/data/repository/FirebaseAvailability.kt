package com.calyx.app.data.repository

/**
 * Checks if Firebase is available at runtime.
 * 
 * TEMPORARY KILL SWITCH: Set FIREBASE_ENABLED = true when ready to use Firebase.
 * All Firebase code remains intact, just disabled for now.
 */
object FirebaseAvailability {
    
    // 🔴 KILL SWITCH: Set to true when you want to enable Firebase
    private const val FIREBASE_ENABLED = false
    
    private var checkedAvailability = false
    private var isAvailable = false
    
    /**
     * Check if Firebase classes are available at runtime.
     * This allows us to compile with Firebase but run without it.
     */
    fun isFirebaseAvailable(): Boolean {
        // Kill switch check first
        if (!FIREBASE_ENABLED) return false
        
        if (checkedAvailability) return isAvailable
        
        isAvailable = try {
            // Try to load Firebase class
            Class.forName("com.google.firebase.database.FirebaseDatabase")
            true
        } catch (e: ClassNotFoundException) {
            false
        } catch (e: Exception) {
            false
        }
        
        checkedAvailability = true
        return isAvailable
    }
    
    /**
     * Check if device is online and Firebase is available.
     */
    fun canUseFirebase(context: android.content.Context): Boolean {
        // Kill switch check first
        if (!FIREBASE_ENABLED) return false
        
        if (!isFirebaseAvailable()) return false
        
        // Check internet connection
        val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) 
            as? android.net.ConnectivityManager
        
        val network = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
