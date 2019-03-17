package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class AddTorrentResultEntity(
        @Json(name = "torrent-added") var torrentAdded: TorrentEntity?,
        @Json(name = "torrent-duplicate") var torrentDuplicate: TorrentEntity?)
