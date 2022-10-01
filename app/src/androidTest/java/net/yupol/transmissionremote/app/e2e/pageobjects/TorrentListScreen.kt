package net.yupol.transmissionremote.app.e2e.pageobjects

import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithIdHidden
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithIdDisplayed
import net.yupol.transmissionremote.app.e2e.utils.waitForCondition

class TorrentListScreen {

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
        fun torrentListScreen(init: TorrentListScreen.() -> Unit) {
            TorrentListScreen().init()
        }
    }
}
