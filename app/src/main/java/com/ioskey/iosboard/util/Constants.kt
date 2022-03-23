package com.ioskey.iosboard.util

import com.ioskey.iosboard.data.KeyboardTheme

const val BUNDLE_THEME_KEY = "theme_key"
const val BUNDLE_IS_EDITING_THEME_KEY = "is_keyboard_was_editing_key"
const val BUNDLE_CROPPED_IMAGE_KEY = "cropped_image_key"

const val EXTRA_LAUNCH_SETTINGS = "launch_settings"

val themesPreset: List<KeyboardTheme> by lazy { getPresetThemes() }
val themesPopular: List<KeyboardTheme> by lazy { themesPreset.take(4) }
val themesOther: List<KeyboardTheme> by lazy { themesPreset.takeLast(10) }
