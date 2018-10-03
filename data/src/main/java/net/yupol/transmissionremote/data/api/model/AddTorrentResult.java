package net.yupol.transmissionremote.data.api.model;

import com.squareup.moshi.Json;

public class AddTorrentResult {
    @Json(name = "torrent-added") public TorrentEntity torrentAdded;
    @Json(name = "torrent-duplicate") public TorrentEntity torrentDuplicate;
}
