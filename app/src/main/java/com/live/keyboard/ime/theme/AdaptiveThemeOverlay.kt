package com.live.keyboard.ime.theme

import android.graphics.Color

/**
 * Theme overlay class which, if enabled, changes some requested attributes in a Theme and returns
 * the corresponding adaptive color. The adaptive colors itself are determined by the ThemeManager
 * and this overlay class only uses them if the feature is enabled in the prefs.
 */
class AdaptiveThemeOverlay(
    private val themeManager: ThemeManager,
    theme: Theme
) : Theme(theme.name, theme.label, theme.authors, theme.isNightTheme, theme.attributes) {
    override fun getAttr(ref: ThemeValue.Reference, s1: String?, s2: String?): ThemeValue {
        return when {
            themeManager.isAdaptiveThemeEnabled -> when (ref) {
                Attr.KEYBOARD_BACKGROUND,
                Attr.KEY_BACKGROUND_PRESSED,
                Attr.SMARTBAR_BACKGROUND,
                Attr.WINDOW_NAVIGATION_BAR_COLOR -> {
                    themeManager.remote.colorPrimaryVariant ?: super.getAttr(ref, s1, s2)
                }
                Attr.KEY_FOREGROUND_PRESSED,
                Attr.SMARTBAR_FOREGROUND -> {
                    themeManager.remote.colorPrimaryVariant?.complimentaryTextColor() ?: super.getAttr(ref, s1, s2)
                }
                Attr.SMARTBAR_FOREGROUND_ALT -> {
                    themeManager.remote.colorPrimaryVariant?.complimentaryTextColor(true) ?: super.getAttr(ref, s1, s2)
                }
                Attr.KEY_BACKGROUND,
                Attr.SMARTBAR_BUTTON_BACKGROUND -> {
                    themeManager.remote.colorPrimary ?: super.getAttr(ref, s1, s2)
                }
                Attr.KEY_FOREGROUND,
                Attr.SMARTBAR_BUTTON_FOREGROUND -> {
                    if (s1 == "shift" && s2 == "capslock") {
                        themeManager.remote.colorSecondary ?: super.getAttr(ref, s1, s2)
                    } else {
                        themeManager.remote.colorPrimary?.complimentaryTextColor() ?: super.getAttr(ref, s1, s2)
                    }
                }
                Attr.KEY_SHOW_BORDER -> {
                    if (themeManager.remote.colorPrimary != null) {
                        ThemeValue.OnOff(true)
                    } else {
                        super.getAttr(ref, s1, s2)
                    }
                }
                Attr.WINDOW_NAVIGATION_BAR_LIGHT -> {
                    if (themeManager.remote.colorPrimaryVariant != null) {
                        ThemeValue.OnOff(themeManager.remote.colorPrimaryVariant?.complimentaryTextColor()?.color == Color.BLACK)
                    } else {
                        super.getAttr(ref, s1, s2)
                    }
                }
                Attr.POPUP_BACKGROUND,
                Attr.GLIDE_TRAIL_COLOR -> {
                    themeManager.remote.colorSecondary ?: super.getAttr(ref, s1, s2)
                }
                Attr.POPUP_BACKGROUND_ACTIVE -> {
                    themeManager.remote.colorSecondary?.complimentaryTextColor(true) ?: super.getAttr(ref, s1, s2)
                }
                Attr.POPUP_FOREGROUND -> {
                    themeManager.remote.colorSecondary?.complimentaryTextColor() ?: super.getAttr(ref, s1, s2)
                }
                else -> super.getAttr(ref, s1, s2)
            }
            else -> super.getAttr(ref, s1, s2)
        }
    }
}
