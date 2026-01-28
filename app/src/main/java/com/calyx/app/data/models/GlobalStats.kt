package com.calyx.app.data.models

/**
 * Global stats model matching the specific structure requested.
 */
data class GlobalStats(
    val total_users: Int = 0,
    val total_global_calls: Long = 0,
    val today: PeriodStats = PeriodStats(),
    val week: PeriodStats = PeriodStats()
)

data class PeriodStats(
    val date: String = "",       // "2025-01-20"
    val week_start: String = "", // "2025-01-13" (Used for week node)
    val calls: Long = 0,
    val active_users: Int = 0
)

data class UserBackendStats(
    val total_calls: Int = 0,
    val last_updated: Long = 0
)
