package net.yupol.transmissionremote.utils

import io.kotlintest.matchers.startWith
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import java.util.concurrent.TimeUnit

class DurationTest: StringSpec({

    "create millis" {
        val duration = Duration.millis(12345)

        duration.duration shouldBe 12345L
        duration.unit shouldBe TimeUnit.MILLISECONDS
    }

    "create seconds" {
        val duration = Duration.seconds(567)

        duration.duration shouldBe 567L
        duration.unit shouldBe TimeUnit.SECONDS
    }

    "convert seconds to millis" {
        val duration = Duration.seconds(42)

        val millis = duration.millis()

        millis shouldBe 42000L
    }

    "convert millis to seconds rounds to floor" {
        val duration = Duration.millis(12999)

        val seconds = duration.seconds()

        seconds shouldBe 12L
    }

    "throws IllegalArgumentException on negative argument" {
        fun testNegativeArgException(init: () -> Unit) {
            val exception = shouldThrow<IllegalArgumentException> {
                init()
            }
            exception.message!! should startWith("Duration must be non negative")
        }

        testNegativeArgException {
            Duration.seconds(-10)
        }

        testNegativeArgException {
            Duration.millis(-150)
        }
    }

    "destructuring duration" {
        val (duration, unit) = Duration.seconds(15)

        duration shouldBe 15L
        unit shouldBe TimeUnit.SECONDS
    }
})