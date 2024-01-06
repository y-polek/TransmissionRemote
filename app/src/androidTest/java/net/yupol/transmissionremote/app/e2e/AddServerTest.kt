package net.yupol.transmissionremote.app.e2e

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import net.yupol.transmissionremote.app.MainActivity
import net.yupol.transmissionremote.app.e2e.robots.AddServerRobot.Companion.addServer
import net.yupol.transmissionremote.app.e2e.robots.TorrentListRobot.Companion.torrentList
import net.yupol.transmissionremote.app.e2e.robots.WelcomeRobot.Companion.welcome
import net.yupol.transmissionremote.mockserver.MockServer
import net.yupol.transmissionremote.mockserver.model.Torrent
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddServerTest {

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
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun add_server() {
        welcome {
            assertAddServerButtonDisplayed()
            clickAddServerButton()
        }
        addServer {
            enterServerName("Test server")
            enterHostName(server.hostName)
            enterPort(server.port)
            clickOk()
        }
        torrentList {
            assertEmptyTorrentList()
        }

        server.addTorrent(
            name = "ubuntu-22.04.3-live-server-amd64.iso",
            size = Torrent.Size(
                totalSize = 2_130_000_000
            )
        )
        server.addTorrent(
            name = "Fedora-KDE-Live-x86_64-38",
            size = Torrent.Size(
                totalSize = 2_410_000_000
            )
        )

        torrentList {
            assertTorrentCount(2)
        }
    }
}
