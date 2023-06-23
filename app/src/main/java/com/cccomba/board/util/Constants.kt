package com.cccomba.board.util

import com.cccomba.board.data.KeyboardTheme

const val BUNDLE_THEME_KEY = "theme_key"
const val BUNDLE_IS_EDITING_THEME_KEY = "is_keyboard_was_editing_key"
const val BUNDLE_CROPPED_IMAGE_KEY = "cropped_image_key"

const val EXTRA_LAUNCH_SETTINGS = "launch_settings"

const val IRON_SOURCE_APP_KEY = "14618a729"

val themesPreset: List<KeyboardTheme> by lazy { getPresetThemes() }
val themesPopular: List<KeyboardTheme> by lazy { themesPreset.take(4) }
val themesOther: List<KeyboardTheme> by lazy { themesPreset.takeLast(10) }