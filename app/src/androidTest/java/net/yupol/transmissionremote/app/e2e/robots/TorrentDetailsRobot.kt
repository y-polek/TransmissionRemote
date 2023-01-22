package net.yupol.transmissionremote.app.e2e.robots

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.ViewPagerActions
import androidx.test.espresso.contrib.ViewPagerActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithIdDisplayed
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithTextDisplayed

class TorrentDetailsRobot {

    fun assertInfoPageOpen() {
        assertViewWithTextDisplayed("Info")
    }

    fun assertFilesPageOpen() {
        assertViewWithTextDisplayed("Files")
    }

    fun assertTrackersPageOpen() {
        assertViewWithTextDisplayed("Trackers")
    }

    fun assertPeersPageOpen() {
        assertViewWithTextDisplayed("Peers")
    }

    fun assertOptionsPageOpen() {
        assertViewWithTextDisplayed("Options")
    }

    fun scrollToInfoPage() {
        scrollToPage(0)
        assertInfoPageOpen()
    }

    fun scrollToFilesPage() {
        scrollToPage(1)
        assertFilesPageOpen()
    }

    fun scrollToTrackersPage() {
        scrollToPage(2)
        assertTrackersPageOpen()
    }

    fun scrollToPeersPage() {
        scrollToPage(3)
        assertPeersPageOpen()
    }

    fun scrollToOptionsPage() {
        scrollToPage(4)
        assertOptionsPageOpen()
    }

    fun exit() {
        Espresso.pressBack()
    }

    private fun scrollToPage(page: Int) {
        onView(withId(R.id.pager)).perform(ViewPagerActions.scrollToPage(page))
    }

    companion object {
        fun torrentDetails(func: TorrentDetailsRobot.() -> Unit): TorrentDetailsRobot {
            assertViewWithIdDisplayed(R.id.pager_title_strip)
            return TorrentDetailsRobot().apply(func)
        }
    }
}
