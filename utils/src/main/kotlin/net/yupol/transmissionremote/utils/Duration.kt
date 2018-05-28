package net.yupol.transmissionremote.utils

import android.support.annotation.IntRange
import java.util.concurrent.TimeUnit

class Duration private constructor(val duration: Long, val unit: TimeUnit) {

    companion object {
        fun millis(@IntRange(from = 0) millis: Long): Duration {
            if (millis < 0) throw IllegalArgumentException("Duration must be non negative")
            return Duration(millis, TimeUnit.MILLISECONDS)
        }
        fun seconds(@IntRange(from = 0) seconds: Long): Duration {
            if (seconds < 0) throw IllegalArgumentException("Duration must be non negative")
            return Duration(seconds, TimeUnit.SECONDS)
        }
    }

    fun millis() = unit.toMillis(duration)
    fun seconds() = unit.toSeconds(duration)

    operator fun component1() = duration
    operator fun component2() = unit
}