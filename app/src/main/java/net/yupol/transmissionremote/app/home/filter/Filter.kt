package net.yupol.transmissionremote.app.home.filter

import androidx.annotation.StringRes
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.model.TorrentViewModel

data class Filter(@StringRes val name: Int, val apply: (TorrentViewModel) -> Boolean)

val FILTERS = listOf(
        Filter(R.string.filter_all) { true },
        Filter(R.string.filter_active, TorrentViewModel::active),
        Filter(R.string.filter_downloading, TorrentViewModel::downloading),
        Filter(R.string.filter_seeding, TorrentViewModel::seeding),
        Filter(R.string.filter_paused, TorrentViewModel::paused),
        Filter(R.string.filter_download_completed, TorrentViewModel::completed),
        Filter(R.string.filter_finished, TorrentViewModel::finished))
