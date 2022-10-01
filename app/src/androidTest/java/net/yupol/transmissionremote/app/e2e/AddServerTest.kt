package net.yupol.transmissionremote.app.e2e

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.yupol.transmissionremote.app.MainActivity
import net.yupol.transmissionremote.app.e2e.mockserver.mockWebServer
import net.yupol.transmissionremote.app.e2e.pageobjects.AddServerScreen.Companion.addServerScreen
import net.yupol.transmissionremote.app.e2e.pageobjects.TorrentListScreen.Companion.torrentListScreen
import net.yupol.transmissionremote.app.e2e.pageobjects.WelcomeScreen.Companion.welcomeScreen
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
            filePath = "session-get.json"
        }
        "torrent-get" to {
            filePath = "torrent-get.json"
            delay = 2.seconds
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
        welcomeScreen {
            assertAddServerButtonDisplayed()
            clickAddServerButton()
        }

        addServerScreen {
            enterServerName("Test server")
            enterHostName(server.hostName)
            enterPort(server.port)
            clickOk()
        }

        torrentListScreen {
            assertProgressbarDisplayed()
            assertProgressbarHidden()
        }
    }
}
