package com.keyboard.neon_keyboard.ime.media.emoticon

import kotlinx.serialization.Serializable

/**
 * Data class for a single emoticon.
 *
 * @property icon The char sequence of the emoticon.
 * @property meaning List of possible meanings for this emoticon.
 */
@Serializable
data class EmoticonKeyData(
    var icon: String = "",
    var meaning: List<String> = listOf()
)
