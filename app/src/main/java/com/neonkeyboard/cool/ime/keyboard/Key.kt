package com.neonkeyboard.cool.ime.keyboard

import android.graphics.Rect

/**
 * Abstract class describing the smallest computed unit in a computed keyboard. Each key represents exactly one key
 * displayed in the UI. It allows to save the absolute location within the parent keyboard, save touch and visual
 * bounds, managing the state (enabled, pressed, visibility) as well as layout sizing factors. Each key in this IME
 * inherits from this base key class. This allows for a inter-operable usage of a key without knowing the exact
 * subclass upfront.
 *
 * @property data The base key data this key represents.This can be anything - from a basic text key to an emoji key
 *  to a complex selector.
 */
abstract class Key(open val data: AbstractKeyData) {
    /**
     * Specifies whether this key is enabled or not.
     */
    open var isEnabled: Boolean = true

    /**
     * Specifies whether this key is actively pressed or not. Is used by the parent keyboard view to draw the key
     * differently to indicate this state.
     */
    open var isPressed: Boolean = false

    /**
     * Specifies whether this key is visible or not. Is used by the parent keyboard view to omit this key in the
     * layout and drawing process. A `false`-value is equivalent to `VISIBILITY_GONE` on Android's View class.
     */
    open var isVisible: Boolean = true

    /**
     * The touch bounds of this key. All bounds defined here are absolute coordinates within the parent keyboard.
     */
    open val touchBounds: Rect = Rect()

    /**
     * The visible bounds of this key. All bounds defined here are absolute coordinates within the parent keyboard.
     */
    open val visibleBounds: Rect = Rect()

    /**
     * The visible drawable bounds of this key. All bounds defined here are absolute coordinates within the parent
     * keyboard.
     */
    open val visibleDrawableBounds: Rect = Rect()

    /**
     * The visible label bounds of this key. All bounds defined here are absolute coordinates within the parent
     * keyboard.
     */
    open val visibleLabelBounds: Rect = Rect()

    /**
     * Specifies how much this key is willing to shrink if too many keys are in a keyboard row. A value of 0.0
     * indicates that the key does not want to shrink in such scenario. This value should not be set manually, only
     * by the key's compute method and is used in the layout process to determine the real key width.
     */
    open var flayShrink: Double = 0.0

    /**
     * Specifies how much this key is willing to grow if too few keys are in a keyboard row. A value of 0.0
     * indicates that the key does not want to grow in such scenario. This value should not be set manually, only
     * by the key's compute method and is used in the layout process to determine the real key width.
     */
    open var flayGrow: Double = 0.0

    /**
     * Specifies the relative proportional width this key aims to get in respective to the keyboard view's desired key
     * width. A value of 1.0 indicates that the key wants to be exactly as wide as the desired key width, a value of
     * 0.0 is basically equivalent to setting [isVisible] to false. This value should not be set manually, only
     * by the key's compute method and is used in the layout process to determine the real key width.
     */
    open var flayWidthFactor: Double = 0.0

    /**
     * The computed UI label of this key. This value is used by the keyboard view to temporarily save the label string
     * for UI rendering and should not be set manually.
     */
    open var label: String? = null

    /**
     * The computed UI hint label of this key. This value is used by the keyboard view to temporarily save the hint
     * label string for UI rendering and should not be set manually.
     */
    open var hintedLabel: String? = null

    /**
     * The computed UI drawable ID of this key. This value is used by the keyboard view to temporarily save the
     * drawable ID for UI rendering and should not be set manually.
     */
    open var foregroundDrawableId: Int? = null
}