package com.calyx.app.data.models

/**
 * Contact information retrieved from the Contacts provider.
 */
data class ContactInfo(
    val contactId: String,
    val displayName: String,
    val photoUri: String?
)
