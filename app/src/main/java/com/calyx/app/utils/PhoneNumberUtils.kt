package com.calyx.app.utils

/**
 * Utility functions for phone number normalization and formatting.
 */
object PhoneNumberUtils {
    
    /**
     * Normalize a phone number by removing all non-digit characters
     * and extracting the last 10 digits for comparison.
     */
    fun normalize(phoneNumber: String): String {
        // Remove all non-digit characters
        val digitsOnly = phoneNumber.filter { it.isDigit() }
        
        // Return last 10 digits for comparison (handles country codes)
        return if (digitsOnly.length > 10) {
            digitsOnly.takeLast(10)
        } else {
            digitsOnly
        }
    }

    /**
     * Format a phone number for display.
     * Example: "5551234567" -> "(555) 123-4567"
     */
    fun formatForDisplay(phoneNumber: String): String {
        val digits = phoneNumber.filter { it.isDigit() }
        
        return when {
            digits.length == 10 -> {
                "(${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
            }
            digits.length == 11 && digits.startsWith("1") -> {
                "+1 (${digits.substring(1, 4)}) ${digits.substring(4, 7)}-${digits.substring(7)}"
            }
            digits.length >= 11 -> {
                val countryCode = digits.dropLast(10)
                val local = digits.takeLast(10)
                "+$countryCode (${local.substring(0, 3)}) ${local.substring(3, 6)}-${local.substring(6)}"
            }
            else -> phoneNumber // Return as-is if we can't format
        }
    }

    /**
     * Check if a phone number is valid (has enough digits).
     */
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val digits = phoneNumber.filter { it.isDigit() }
        return digits.length >= 3 // Allow short codes
    }

    /**
     * Check if the number is a private/unknown caller.
     */
    fun isPrivateNumber(phoneNumber: String): Boolean {
        val lower = phoneNumber.lowercase()
        return lower.contains("private") || 
               lower.contains("unknown") || 
               lower.contains("blocked") ||
               phoneNumber.isBlank() ||
               phoneNumber == "-1" ||
               phoneNumber == "-2"
    }
    
    /**
     * Get display name with privacy considerations.
     * - If contact has a name: return the name
     * - If unknown/unsaved: return just the masked phone number (no "Unknown" prefix)
     */
    fun getDisplayName(contactName: String?, phoneNumber: String): String {
        val hasRealName = !contactName.isNullOrBlank() && 
                          contactName != phoneNumber &&
                          !isLikelyPhoneNumber(contactName)
        
        return if (hasRealName) {
            contactName!!
        } else {
            maskPhoneNumber(phoneNumber)
        }
    }
    
    /**
     * Check if a contact is unsaved (no real contact name, just a phone number).
     * This can be used to show a distinctive icon for unsaved contacts.
     */
    fun isUnsavedContact(contactName: String?, phoneNumber: String): Boolean {
        val hasRealName = !contactName.isNullOrBlank() && 
                          contactName != phoneNumber &&
                          !isLikelyPhoneNumber(contactName)
        return !hasRealName
    }
    
    /**
     * Mask a phone number for privacy.
     * Format: +880 171 *** **89 (shows first 3 and last 2 digits)
     */
    fun maskPhoneNumber(phoneNumber: String): String {
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }
        
        if (cleaned.length < 6) {
            return "(*****)"
        }
        
        return when {
            cleaned.startsWith("+") -> {
                val digits = cleaned.drop(1)
                when {
                    digits.length >= 10 -> {
                        val countryCode = when {
                            digits.startsWith("880") -> "+880"
                            digits.startsWith("1") && digits.length == 11 -> "+1"
                            digits.startsWith("44") -> "+44"
                            digits.startsWith("91") -> "+91"
                            else -> "+${digits.take(2)}"
                        }
                        val remaining = digits.drop(countryCode.length - 1)
                        val visible = remaining.take(3)
                        val lastTwo = remaining.takeLast(2)
                        "($countryCode $visible***$lastTwo)"
                    }
                    else -> "(****${digits.takeLast(2)})"
                }
            }
            cleaned.length >= 10 -> {
                val visible = cleaned.take(3)
                val lastTwo = cleaned.takeLast(2)
                "($visible***$lastTwo)"
            }
            else -> "(****${cleaned.takeLast(2)})"
        }
    }
    
    /**
     * Check if a string looks like a phone number rather than a name.
     */
    private fun isLikelyPhoneNumber(text: String): Boolean {
        val digitsCount = text.count { it.isDigit() }
        val totalLength = text.length
        return totalLength > 0 && (digitsCount.toFloat() / totalLength) > 0.6f
    }
}
