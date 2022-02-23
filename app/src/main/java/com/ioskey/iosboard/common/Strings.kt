@file:Suppress("NOTHING_TO_INLINE")

package com.ioskey.iosboard.common

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <R> stringBuilder(builder: StringBuilder.() -> R): String {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val sb = StringBuilder()
    builder(sb)
    return sb.toString()
}

inline fun String.lowercase(locale: FlorisLocale): String = this.lowercase(locale.base)

inline fun String.uppercase(locale: FlorisLocale): String = this.uppercase(locale.base)
