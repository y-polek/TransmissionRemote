package net.yupol.transmissionremote.app.e2e

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import net.yupol.transmissionremote.app.MainActivity
import net.yupol.transmissionremote.app.e2e.mockserver.mockWebServer
import net.yupol.transmissionremote.app.e2e.robots.AddServerRobot.Companion.addServer
import net.yupol.transmissionremote.app.e2e.robots.TorrentDetailsInfoPageRobot.Companion.torrentDetailsInfoPage
import net.yupol.transmissionremote.app.e2e.robots.TorrentDetailsRobot.Companion.torrentDetails
import net.yupol.transmissionremote.app.e2e.robots.TorrentListRobot.Companion.torrentList
import net.yupol.transmissionremote.app.e2e.robots.WelcomeRobot.Companion.welcome
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RunWith(AndroidJUnit4::class)
@LargeTest
class TorrentDetailsInfoPageTest {

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
    fun torrent_details_info_page_test() {
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
            clickTorrentAtPosition(9)
        }
        torrentDetails {
            scrollToInfoPage()
        }
        torrentDetailsInfoPage {
            assertTotalSizeText("3.6 GB (14599 pieces of 256.0 KB)")
            assertLocation("/Users/ypolek/Downloads")
            assertPrivacy("Public Torrent")
            assertCreator("mktorrent 1.1")
            assertCreatedOn(1660215352000.formatDate())
            assertComment("Ubuntu CD releases.ubuntu.com")
            assertMagnet("magnet:?xt=urn:btih:3b245504cf5f11bbdbe1201cea6a6bf45aee1bc0&dn=ubuntu-22.04.1-desktop-amd64.iso&tr=https://torrent.ubuntu.com/announce&tr=https://ipv6.torrent.ubuntu.com/announce")
            assertHave("131.1 MB of 3.6 GB (4.3%), 25.1 MB Unverified")
            assertAvailable("100.0%")
            assertDownloaded("156.6 MB")
            assertUploaded("336.3 KB (Ratio: 0.00)")
            assertAverageSpeed("374.5 KB/s")
            assertAdded(1674401936000.formatDate())
            assertLastActivity(1674403869000.formatDate())
            assertDownloading("7m 7s")
            assertSeeding("1m 5s")
        }
    }

    private fun Long.formatDate() = SimpleDateFormat(
        "MMM dd, yyyy 'at' h:mm a",
        Locale.getDefault()
    ).format(Date(this))
}
