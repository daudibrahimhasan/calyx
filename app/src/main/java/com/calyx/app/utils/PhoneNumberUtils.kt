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
}
