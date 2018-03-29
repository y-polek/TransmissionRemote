package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.model.json.Torrent;

import java.util.Collection;

public class StopTorrentRequest extends TorrentActionRequest {

    public StopTorrentRequest(int... torrentIds) {
        super("torrent-stop", torrentIds);
    }

    public StopTorrentRequest(Collection<Torrent> torrents) {
        this(toIds(torrents));
    }
}
