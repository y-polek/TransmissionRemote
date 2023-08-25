package net.yupol.transmissionremote.app.e2e.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ConditionWatcher(
    private val timeoutLimit: Duration = 15.seconds,
    private val watchInterval: Duration = 250.milliseconds
) {
    @Throws(Exception::class)
    fun waitForCondition(description: String, condition: Condition) {
        var status = Status.CONDITION_NOT_MET
        var elapsedTime = Duration.ZERO
        do {
            if (condition.check()) {
                status = Status.CONDITION_MET
            } else {
                elapsedTime += watchInterval
                Thread.sleep(watchInterval.inWholeMilliseconds)
            }
            if (elapsedTime >= timeoutLimit) {
                status = Status.TIMEOUT
                break
            }
        } while (status != Status.CONDITION_MET)

        if (status == Status.TIMEOUT) {
            throw IllegalStateException(
                "'${description}' - took more than " + timeoutLimit.inWholeMilliseconds + " milliseconds",
                condition.exception
            )
        }
    }

    abstract class Condition {

        var exception: Throwable? = null
            private set

        @Throws(Throwable::class)
        abstract fun checkCondition()

        fun check(): Boolean {
            return try {
                checkCondition()
                true
            } catch (e: Throwable) {
                exception = e
                false
            }
        }
    }

    private enum class Status {
        CONDITION_NOT_MET, CONDITION_MET, TIMEOUT
    }
}
