package com.ioskey.iosboard.common

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.isVisible

/**
 * This file has been taken from the Android LatinIME project. Following modifications were done to
 * the original source code:
 * - Convert the code from Java to Kotlin
 * - Change package name to match the current project's one
 * - Remove method newLayoutParam()
 * - Remove method placeViewAt()
 * - Remove unnecessary variable params in updateLayoutGravityOf(), lp can directly be used due to
 *    Kotlin's smart cast feature
 * - Remove unused imports
 *
 * The original source code can be found at the following location:
 *  https://android.googlesource.com/platform/packages/inputmethods/LatinIME/+/refs/heads/master/java/src/com/android/inputmethod/latin/utils/ViewLayoutUtils.java
 */
object ViewUtils {
    fun updateLayoutHeightOf(window: Window, layoutHeight: Int) {
        val params = window.attributes
        if (params != null && params.height != layoutHeight) {
            params.height = layoutHeight
            window.attributes = params
        }
    }

    fun updateLayoutHeightOf(view: View, layoutHeight: Int) {
        val params = view.layoutParams
        if (params != null && params.height != layoutHeight) {
            params.height = layoutHeight
            view.layoutParams = params
        }
    }

    fun updateLayoutGravityOf(view: View, layoutGravity: Int) {
        val lp = view.layoutParams
        if (lp is LinearLayout.LayoutParams) {
            if (lp.gravity != layoutGravity) {
                lp.gravity = layoutGravity
                view.layoutParams = lp
            }
        } else if (lp is FrameLayout.LayoutParams) {
            if (lp.gravity != layoutGravity) {
                lp.gravity = layoutGravity
                view.layoutParams = lp
            }
        } else {
            throw IllegalArgumentException(
                "Layout parameter doesn't have gravity: "
                        + lp.javaClass.name
            )
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * Source: https://stackoverflow.com/a/9563438/6801193 (by Muhammad Nabeel Arif)
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun dp2px(dp: Float): Float {
        return dp * (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * Source: https://stackoverflow.com/a/9563438/6801193 (by Muhammad Nabeel Arif)
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @return A float value to represent dp equivalent to px value
     */
    fun px2dp(px: Float): Float {
        return px / (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun setEnabled(view: View, v: Boolean) {
        view.isEnabled = v
        if (view is ViewGroup) {
            for (childView in view.children) {
                setEnabled(childView, v)
            }
        }
    }

    fun setVisible(view: View, v: Boolean) {
        view.isVisible = v
        if (view is ViewGroup) {
            for (childView in view.children) {
                setVisible(childView, v)
            }
        }
    }
}