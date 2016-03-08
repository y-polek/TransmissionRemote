package net.yupol.transmissionremote.app.model.json;

import com.google.api.client.util.Key;

public class AddTorrentResult {
    @Key("torrent-added") public Torrent torrentAdded;
    @Key("torrent-duplicate") public Torrent torrentDuplicate;
}
