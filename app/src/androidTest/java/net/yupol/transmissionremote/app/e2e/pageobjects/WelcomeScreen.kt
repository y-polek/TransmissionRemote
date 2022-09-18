package net.yupol.transmissionremote.app.e2e.pageobjects

import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.e2e.utils.assertIdDisplayed
import net.yupol.transmissionremote.app.e2e.utils.clickId
import net.yupol.transmissionremote.app.e2e.utils.waitForCondition

class WelcomeScreen {

    fun assertAddServerButtonDisplayed() {
        waitForCondition("Add server button displayed") {
            assertIdDisplayed(R.id.add_server_button)
        }
    }

    fun clickAddServerButton() {
        clickId(R.id.add_server_button)
    }

    companion object {
        fun welcomeScreen(init: WelcomeScreen.() -> Unit) {
            WelcomeScreen().init()
        }
    }
}
