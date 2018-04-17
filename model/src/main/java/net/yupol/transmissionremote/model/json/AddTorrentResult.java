package net.yupol.transmissionremote.model.json;

import com.squareup.moshi.Json;

public class AddTorrentResult {
    @Json(name = "torrent-added") public Torrent torrentAdded;
    @Json(name = "torrent-duplicate") public Torrent torrentDuplicate;
}
