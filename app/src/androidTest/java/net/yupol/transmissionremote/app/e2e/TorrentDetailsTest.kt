package net.yupol.transmissionremote.app.e2e

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import net.yupol.transmissionremote.app.MainActivity
import net.yupol.transmissionremote.app.e2e.robots.AddServerRobot.Companion.addServer
import net.yupol.transmissionremote.app.e2e.robots.TorrentDetailsRobot.Companion.torrentDetails
import net.yupol.transmissionremote.app.e2e.robots.TorrentListRobot.Companion.torrentList
import net.yupol.transmissionremote.app.e2e.robots.WelcomeRobot.Companion.welcome
import net.yupol.transmissionremote.mockserver.MockServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TorrentDetailsTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<MainActivity>()

    @get:Rule
    val grantNotificationsPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    private val server = MockServer()

    @Before
    fun setup() {
        server.start()
        server.addTorrent(
            name = "openSUSE-Leap-15.4-NET-x86_64-Build243.2-Media.iso",
            totalSize = 181403648L
        )
        server.addTorrent(
            name = "ubuntu-22.04.1-desktop-amd64.iso",
            totalSize = 3826831360L
        )
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun torrent_details_test() {
        welcome {
            clickAddServerButton()
        }
        addServer {
            enterServerName("Test server")
            enterHostName(server.hostName)
            enterPort(server.port)
            clickOk()
        }
        torrentList {
            clickTorrentAtPosition(0)
        }
        torrentDetails {
            assertInfoPageOpen()
            scrollToFilesPage()
            scrollToTrackersPage()
            scrollToPeersPage()
            scrollToOptionsPage()
            exit()
        }
        torrentList {
            clickTorrentAtPosition(1)
        }
        torrentDetails {
            assertOptionsPageOpen()
        }
    }
}
