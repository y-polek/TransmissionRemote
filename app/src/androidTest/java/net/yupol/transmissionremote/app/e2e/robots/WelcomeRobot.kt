package net.yupol.transmissionremote.app.e2e.robots

import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithIdDisplayed
import net.yupol.transmissionremote.app.e2e.utils.clickId
import net.yupol.transmissionremote.app.e2e.utils.waitForCondition

class WelcomeRobot {

    fun assertAddServerButtonDisplayed() {
        waitForCondition("Add server button displayed") {
            assertViewWithIdDisplayed(R.id.add_server_button)
        }
    }

    infix fun clickAddServerButton(func: AddServerRobot.() -> Unit): AddServerRobot {
        clickId(R.id.add_server_button)
        return AddServerRobot().apply(func)
    }

    companion object {
        fun welcome(func: WelcomeRobot.() -> Unit) = WelcomeRobot().apply { func() }
    }
}
