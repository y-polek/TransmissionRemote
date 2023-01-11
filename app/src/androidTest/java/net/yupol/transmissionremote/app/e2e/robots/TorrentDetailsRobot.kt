package net.yupol.transmissionremote.app.e2e.robots

import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithIdDisplayed

class TorrentDetailsRobot {

    companion object {
        fun torrentDetails(func: TorrentDetailsRobot.() -> Unit): TorrentDetailsRobot {
            assertViewWithIdDisplayed(R.id.pager_title_strip)
            return TorrentDetailsRobot().apply(func)
        }
    }
}
