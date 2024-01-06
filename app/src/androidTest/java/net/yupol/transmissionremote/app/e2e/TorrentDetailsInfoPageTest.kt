package net.yupol.transmissionremote.app.e2e

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import net.yupol.transmissionremote.app.MainActivity
import net.yupol.transmissionremote.app.e2e.robots.AddServerRobot.Companion.addServer
import net.yupol.transmissionremote.app.e2e.robots.TorrentDetailsInfoPageRobot.Companion.torrentDetailsInfoPage
import net.yupol.transmissionremote.app.e2e.robots.TorrentDetailsRobot.Companion.torrentDetails
import net.yupol.transmissionremote.app.e2e.robots.TorrentListRobot.Companion.torrentList
import net.yupol.transmissionremote.app.e2e.robots.WelcomeRobot.Companion.welcome
import net.yupol.transmissionremote.mockserver.MockServer
import net.yupol.transmissionremote.mockserver.model.Torrent
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

    @get:Rule
    val grantNotificationsPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    private val server = MockServer()

    @Before
    fun setup() {
        server.start()
        server.addTorrent(
            name = "ubuntu-22.04.1-desktop-amd64.iso",
            size = Torrent.Size(
                totalSize = 3826831360,
                sizeWhenDone = 3826831360,
                leftUntilDone = 3663085568,
                desiredAvailable = 3663085568,
                haveUnchecked = 26329088,
                haveValid = 137416704,
                downloadedEver = 164165771,
                uploadedEver = 344337,
                pieceSize = 262144,
                pieceCount = 14599
            ),
            dates = Torrent.Dates(
                added = 1674401936,
                created = 1660215352,
                activity = 1674403869
            ),
            secondsDownloading = 427,
            secondsSeeding = 65,
            downloadDir = "~/Downloads",
            creator = "Transmission 3.0",
            comment = "Ubuntu CD releases.ubuntu.com",
            magnetLink = "magnet:?xt=urn:btih:3b245504cf5f11bbdbe1201cea6a6bf45aee1bc0&dn=ubuntu-22.04.1-desktop-amd64.iso&tr=https%3A%2F%2Ftorrent.ubuntu.com%2Fannounce&tr=https%3A%2F%2Fipv6.torrent.ubuntu.com%2Fannounce"
        )
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
            clickTorrentAtPosition(0)
        }
        torrentDetails {
            scrollToInfoPage()
        }
        torrentDetailsInfoPage {
            assertTotalSizeText("3.6 GB (14599 pieces of 256.0 KB)")
            assertLocation("~/Downloads")
            assertPrivacy("Public Torrent")
            assertCreator("Transmission 3.0")
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
