package com.calyx.app.data.models

/**
 * Represents a single call record from the system call log.
 */
data class CallEntry(
    val id: Long,
    val phoneNumber: String,
    val callType: Int,
    val date: Long,
    val duration: Long,
    val contactName: String? = null,
    val photoUri: String? = null
) {
    companion object {
        const val TYPE_INCOMING = 1
        const val TYPE_OUTGOING = 2
        const val TYPE_MISSED = 3
        const val TYPE_VOICEMAIL = 4
        const val TYPE_REJECTED = 5
    }

    val isIncoming: Boolean get() = callType == TYPE_INCOMING
    val isOutgoing: Boolean get() = callType == TYPE_OUTGOING
    val isMissed: Boolean get() = callType == TYPE_MISSED
}
