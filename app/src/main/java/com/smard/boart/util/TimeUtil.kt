package com.smard.boart.util

import java.util.*

object TimeUtil {
    fun decode(v: Int): Time {
        return Time(
            hour = (v shr 8) and 0xFF,
            minute = v and 0xFF
        )
    }

    fun encode(v: Time): Int {
        return encode(v.hour, v.minute)
    }

    fun encode(h: Int, m: Int): Int {
        return ((h shl 8) and 0xFF00) + (m and 0xFF)
    }

    fun asString(v: Time): String {
        return String.format("%02d:%02d", v.hour, v.minute)
    }

    fun currentLocalTime(): Time {
        val rightNow = Calendar.getInstance()
        return Time(
            hour = rightNow[Calendar.HOUR_OF_DAY],
            minute = rightNow[Calendar.MINUTE]
        )
    }

    fun isNightTime(sunrise: Time, sunset: Time, current: Time): Boolean {
        return isNightTime(encode(sunrise), encode(sunset), encode(current))
    }

    fun isNightTime(sunrise: Int, sunset: Int, current: Int): Boolean {
        return if (sunrise <= sunset) {
            current !in sunrise..sunset
        } else {
            current in sunset..sunrise
        }
    }

    data class Time(val hour: Int, val minute: Int)
}
