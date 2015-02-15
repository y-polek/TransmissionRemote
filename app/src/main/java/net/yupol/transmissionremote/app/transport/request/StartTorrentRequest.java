package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.app.model.json.Torrent;

import java.util.Collection;

public class StartTorrentRequest extends TorrentActionRequest {

    public StartTorrentRequest(Collection<Torrent> torrents) {
        super("torrent-start-now", torrents);
    }
}
