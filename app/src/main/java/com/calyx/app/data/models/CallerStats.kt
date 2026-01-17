package com.calyx.app.data.models

/**
 * Primary data model representing aggregated statistics for each unique caller.
 */
data class CallerStats(
    val contactId: String? = null,
    val phoneNumber: String,
    val displayName: String,
    val profilePhotoUri: String? = null,
    val totalCalls: Int = 0,
    val incomingCalls: Int = 0,
    val outgoingCalls: Int = 0,
    val missedCalls: Int = 0,
    val totalDuration: Long = 0L,
    val averageDuration: Long = 0L,
    val firstCallDate: Long = 0L,
    val lastCallDate: Long = 0L,
    var rankByCount: Int = 0,
    var rankByDuration: Int = 0
) {
    /**
     * Get the appropriate rank based on the category
     */
    fun getRank(category: RankingCategory): Int {
        return when (category) {
            RankingCategory.MOST_CALLED -> rankByCount
            RankingCategory.MOST_TALKED -> rankByDuration
        }
    }

    /**
     * Get initials from display name for avatar fallback
     */
    fun getInitials(): String {
        val parts = displayName.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts.first().firstOrNull()?.uppercaseChar() ?: ""}${parts.last().firstOrNull()?.uppercaseChar() ?: ""}"
            parts.isNotEmpty() -> parts.first().take(2).uppercase()
            else -> "?"
        }
    }
}
