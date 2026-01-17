package com.calyx.app.data.models

/**
 * Enum defining available time period filters.
 */
enum class TimeRange(val displayName: String) {
    WEEKLY("Weekly"),
    ALL_TIME("All Time");

    companion object {
        const val WEEK_IN_MILLIS = 7 * 24 * 60 * 60 * 1000L
    }
}
