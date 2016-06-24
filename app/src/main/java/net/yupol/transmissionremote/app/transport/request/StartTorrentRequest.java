package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.app.model.json.Torrent;

import java.util.Collection;

public class StartTorrentRequest extends TorrentActionRequest {

    public StartTorrentRequest(int[] torrentIds, boolean noQueue) {
        super(noQueue ? "torrent-start-now" : "torrent-start", torrentIds);
    }

    public StartTorrentRequest(int... torrentIds) {
        this(torrentIds, false);
    }

    public StartTorrentRequest(Collection<Torrent> torrents) {
        this(toIds(torrents), false);
    }
}
