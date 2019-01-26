package net.yupol.transmissionremote.app.home.filter

import androidx.annotation.StringRes
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.model.TorrentViewModel

data class Filter(
        @StringRes val name: Int,
        @StringRes val emptyMsg: Int,
        private val description: String,
        val apply: (TorrentViewModel) -> Boolean)
{
    override fun toString(): String {
        return description
    }
}

val FILTERS = listOf(
        Filter(R.string.filter_all, R.string.filter_empty_all, "All") { true },
        Filter(R.string.filter_active, R.string.filter_empty_active, "Active", TorrentViewModel::active),
        Filter(R.string.filter_downloading, R.string.filter_empty_downloading, "Downloading", TorrentViewModel::downloading),
        Filter(R.string.filter_seeding, R.string.filter_empty_seeding, "Seeding", TorrentViewModel::seeding),
        Filter(R.string.filter_paused, R.string.filter_empty_paused, "Paused", TorrentViewModel::paused),
        Filter(R.string.filter_download_completed, R.string.filter_empty_download_completed, "Downloaded", TorrentViewModel::completed),
        Filter(R.string.filter_finished, R.string.filter_empty_finished, "Finished", TorrentViewModel::finished))
