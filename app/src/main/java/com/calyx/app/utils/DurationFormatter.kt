package com.calyx.app.utils

/**
 * Utility functions for formatting call durations.
 */
object DurationFormatter {

    /**
     * Format duration in seconds to human-readable format.
     * - Less than 60 seconds: "X sec"
     * - Less than 60 minutes: "X min"
     * - 60+ minutes: "X hr Y min"
     */
    fun format(seconds: Long): String {
        return when {
            seconds < 60 -> "$seconds sec"
            seconds < 3600 -> "${seconds / 60} min"
            else -> {
                val hours = seconds / 3600
                val mins = (seconds % 3600) / 60
                if (mins > 0) "$hours hr $mins min" else "$hours hr"
            }
        }
    }

    /**
     * Format duration for detailed display.
     * Example: "2 hours 15 minutes total"
     */
    fun formatDetailed(seconds: Long): String {
        return when {
            seconds < 60 -> "$seconds seconds"
            seconds < 3600 -> "${seconds / 60} minutes"
            else -> {
                val hours = seconds / 3600
                val mins = (seconds % 3600) / 60
                val hourText = if (hours == 1L) "hour" else "hours"
                val minText = if (mins == 1L) "minute" else "minutes"
                if (mins > 0) "$hours $hourText $mins $minText" else "$hours $hourText"
            }
        }
    }

    /**
     * Format duration in short form for list items.
     * Example: "2h 15m" or "45m" or "30s"
     */
    fun formatShort(seconds: Long): String {
        return when {
            seconds < 60 -> "${seconds}s"
            seconds < 3600 -> "${seconds / 60}m"
            else -> {
                val hours = seconds / 3600
                val mins = (seconds % 3600) / 60
                if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
            }
        }
    }

    /**
     * Format average duration.
     * Example: "4.5 min/call"
     */
    fun formatAverage(totalSeconds: Long, totalCalls: Int): String {
        if (totalCalls == 0) return "0 min/call"
        val avgSeconds = totalSeconds / totalCalls
        val avgMinutes = avgSeconds / 60.0
        return if (avgMinutes < 1) {
            "${avgSeconds}s/call"
        } else {
            String.format("%.1f min/call", avgMinutes)
        }
    }
}
