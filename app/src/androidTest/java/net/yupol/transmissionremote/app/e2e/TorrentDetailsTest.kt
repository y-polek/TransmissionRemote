package net.yupol.transmissionremote.app.e2e

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.yupol.transmissionremote.app.MainActivity
import net.yupol.transmissionremote.app.e2e.mockserver.mockWebServer
import net.yupol.transmissionremote.app.e2e.robots.AddServerRobot.Companion.addServer
import net.yupol.transmissionremote.app.e2e.robots.TorrentDetailsRobot.Companion.torrentDetails
import net.yupol.transmissionremote.app.e2e.robots.TorrentListRobot.Companion.torrentList
import net.yupol.transmissionremote.app.e2e.robots.WelcomeRobot.Companion.welcome
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TorrentDetailsTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    private val server = mockWebServer {
        "session-get" to {
            response = {
                filePath = "session-get.json"
            }
        }
        "torrent-get" to {
            response = {
                filePath = "torrent-get.json"
            }
        }
        "torrent-get" to {
            ids = listOf(1)
            response = {
                filePath = "torrent-get-1.json"
            }
        }
        "torrent-get" to {
            ids = listOf(2)
            response = {
                filePath = "torrent-get-2.json"
            }
        }
    }

    @Before
    fun setup() {
        server.start()
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
            clickTorrentAtPosition(7)
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
            clickTorrentAtPosition(9)
        }
        torrentDetails {
            assertOptionsPageOpen()
        }
    }
}
