package net.yupol.transmissionremote.app.home.filter

import androidx.annotation.StringRes
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.model.TorrentViewModel

data class Filter(
        @StringRes val name: Int,
        private val description: String,
        val apply: (TorrentViewModel) -> Boolean)
{
    override fun toString(): String {
        return description
    }
}

val FILTERS = listOf(
        Filter(R.string.filter_all, "All") { true },
        Filter(R.string.filter_active, "Active", TorrentViewModel::active),
        Filter(R.string.filter_downloading, "Downloading", TorrentViewModel::downloading),
        Filter(R.string.filter_seeding, "Seeding", TorrentViewModel::seeding),
        Filter(R.string.filter_paused, "Paused", TorrentViewModel::paused),
        Filter(R.string.filter_download_completed, "Downloaded", TorrentViewModel::completed),
        Filter(R.string.filter_finished, "Finished", TorrentViewModel::finished))
