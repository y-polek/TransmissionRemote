package net.yupol.transmissionremote.app.e2e

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.yupol.transmissionremote.app.MainActivity
import net.yupol.transmissionremote.app.e2e.mockserver.mockWebServer
import net.yupol.transmissionremote.app.e2e.robots.AddServerRobot.Companion.addServer
import net.yupol.transmissionremote.app.e2e.robots.ProgressRobot.Companion.progress
import net.yupol.transmissionremote.app.e2e.robots.TorrentListRobot.Companion.torrentList
import net.yupol.transmissionremote.app.e2e.robots.WelcomeRobot.Companion.welcome
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddServerTest {

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
                delay = 2.seconds
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
        progress {
            assertProgressbarDisplayed()
            assertProgressbarHidden()
        }
        torrentList {
            assertTorrentCount(10)
        }
    }
}
