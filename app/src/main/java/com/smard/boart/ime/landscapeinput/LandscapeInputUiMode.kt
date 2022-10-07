package com.smard.boart.ime.landscapeinput

enum class LandscapeInputUiMode {
    DYNAMICALLY_SHOW,
    NEVER_SHOW,
    ALWAYS_SHOW;

    companion object {
        fun fromString(string: String): LandscapeInputUiMode {
            return valueOf(string.uppercase())
        }
    }
}
