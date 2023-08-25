package net.yupol.transmissionremote.app.e2e.robots

import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithIdDisplayed
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithIdExists
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithIdHidden
import net.yupol.transmissionremote.app.e2e.utils.waitForCondition

class ProgressRobot {

    fun assertProgressbarDisplayed() {
        waitForCondition("Progressbar displayed") {
            assertViewWithIdDisplayed(R.id.progressbar)
        }
    }

    fun assertProgressbarHidden() {
        waitForCondition("Progressbar hidden") {
            assertViewWithIdHidden(R.id.progressbar)
        }
    }

    companion object {
        fun progress(func: ProgressRobot.() -> Unit): ProgressRobot {
            assertViewWithIdExists(R.id.progressbar)
            return ProgressRobot().apply(func)
        }
    }
}
