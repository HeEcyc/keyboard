package com.gg.osto.ime.text.key

/**
 * Helper class for summarizing all hint preferences in one single object.
 */
data class KeyHintConfiguration(
    val symbolHintMode: KeyHintMode,
    val numberHintMode: KeyHintMode,
    val mergeHintPopups: Boolean
) {
    companion object {
        val HINTS_DISABLED = KeyHintConfiguration(KeyHintMode.DISABLED, KeyHintMode.DISABLED, false)
    }
}
