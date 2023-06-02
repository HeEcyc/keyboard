package com.cccomba.board.ime.keyboard

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Abstract class definition for providing icons to a keyboard view. This class has been introduced to remove the need
 * for keyboard vies to re-fetch drawable resources every time they draw on the canvas. The exact implementation is
 * dependent on the subclass.
 */
abstract class KeyboardIconSet {
    /**
     * Get the drawable for the given [id].
     *
     * @param id The Android resource id of the drawable which should be returned.
     *
     * @return The drawable for given [id] or null if this icon set does not contain a drawable for this id.
     */
    abstract fun getDrawable(@DrawableRes id: Int): Drawable?

    /**
     * Performs [block] on the drawable with the given [id]. If no drawable for the id exists,[block] will not be
     * called at all.
     *
     * @param id The Android resource id of the drawable which should be used to execute block with.
     * @param block The block which should be executed with the returned drawable.
     */
    inline fun withDrawable(@DrawableRes id: Int, block: Drawable.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.AT_MOST_ONCE)
        }
        val drawable = getDrawable(id)
        if (drawable != null) {
            synchronized(drawable) {
                block(drawable)
            }
        }
    }
}
