package net.yupol.transmissionremote.app.e2e.robots

import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithIdHasText
import net.yupol.transmissionremote.app.e2e.utils.assertViewWithTextDisplayed

class TorrentDetailsInfoPageRobot {

    fun assertTotalSizeText(text: String) {
        assertViewWithIdHasText(R.id.total_size_text, text)
    }

    fun assertLocation(text: String) {
        assertViewWithIdHasText(R.id.location_text, text)
    }

    fun assertPrivacy(text: String) {
        assertViewWithIdHasText(R.id.privacy_text, text)
    }

    fun assertCreator(text: String) {
        assertViewWithIdHasText(R.id.creator_text, text)
    }

    fun assertCreatedOn(text: String) {
        assertViewWithIdHasText(R.id.created_on_text, text)
    }

    fun assertComment(text: String) {
        assertViewWithIdHasText(R.id.comment_text, text)
    }

    fun assertMagnet(text: String) {
        assertViewWithIdHasText(R.id.magnet_text, text)
    }

    fun assertHave(text: String) {
        assertViewWithIdHasText(R.id.have_text, text)
    }

    fun assertAvailable(text: String) {
        assertViewWithIdHasText(R.id.available_text, text)
    }

    fun assertDownloaded(text: String) {
        assertViewWithIdHasText(R.id.downloaded_text, text)
    }

    fun assertUploaded(text: String) {
        assertViewWithIdHasText(R.id.uploaded_text, text)
    }

    fun assertAverageSpeed(text: String) {
        assertViewWithIdHasText(R.id.average_speed_text, text)
    }

    fun assertAdded(text: String) {
        assertViewWithIdHasText(R.id.added_text, text)
    }

    fun assertLastActivity(text: String) {
        assertViewWithIdHasText(R.id.last_activity_text, text)
    }

    fun assertDownloading(text: String) {
        assertViewWithIdHasText(R.id.downloading_text, text)
    }

    fun assertSeeding(text: String) {
        assertViewWithIdHasText(R.id.seeding_text, text)
    }

    companion object {
        fun torrentDetailsInfoPage(
            func: TorrentDetailsInfoPageRobot.() -> Unit
        ): TorrentDetailsInfoPageRobot {
            assertViewWithTextDisplayed("Info")
            assertViewWithTextDisplayed("Torrent Information")
            assertViewWithTextDisplayed("Transfer")
            assertViewWithTextDisplayed("Dates")
            assertViewWithTextDisplayed("Time Elapsed")
            return TorrentDetailsInfoPageRobot().apply(func)
        }
    }
}
