package com.live.keyboard.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.live.keyboard.FlorisApplication
import kotlin.reflect.KClass

fun getColorFromAttr(
    context: Context,
    attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    context.theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

fun getBooleanFromAttr(
    context: Context,
    attrBoolean: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Boolean {
    context.theme.resolveAttribute(attrBoolean, typedValue, resolveRefs)
    return typedValue.data != 0
}

fun setBackgroundTintColor(view: View, colorId: Int) {
    view.backgroundTintList = ColorStateList.valueOf(
        getColorFromAttr(view.context, colorId)
    )
}

fun setBackgroundTintColor2(view: View, colorInt: Int) {
    view.backgroundTintList = ColorStateList.valueOf(colorInt)
}

fun refreshLayoutOf(view: View?) {
    if (view is ViewGroup) {
        view.invalidate()
        view.requestLayout()
        for (childView in view.children) {
            refreshLayoutOf(childView)
        }
    } else {
        view?.invalidate()
        view?.requestLayout()
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.findViewWithType(type: KClass<T>): T? {
    for (child in this.children) {
        if (type.isInstance(child)) {
            return child as T
        } else if (child is ViewGroup) {
            child.findViewWithType(type)?.let { return it }
        }
    }
    return null
}

/**
 * Context extension function to get the Activity from the Context. Originally written by Vlad as
 * an SO answer. Modified to return an AppCompatActivity, as FlorisBoard relies on some compat
 * stuff.
 *
 * Original source: https://stackoverflow.com/a/58249983/6801193
 */
tailrec fun Context?.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    else -> (this as? ContextWrapper)?.baseContext?.getActivity()
}

fun View.addInto(viewGroup: ViewGroup) = viewGroup.addView(this)

fun Context.dpToPx(dp: Int) = (dp.toFloat() * resources.displayMetrics.density).toInt()

fun Int.toPx() = FlorisApplication.instance.applicationContext.dpToPx(this)

