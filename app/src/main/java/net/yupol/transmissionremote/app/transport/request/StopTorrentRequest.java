package net.yupol.transmissionremote.app.transport.request;

import net.yupol.transmissionremote.app.model.json.Torrent;

import java.util.Collection;

public class StopTorrentRequest extends TorrentActionRequest {

    public StopTorrentRequest(Collection<Torrent> torrents) {
        super(torrents);
    }

    @Override
    protected String getMethod() {
        return "torrent-stop";
    }
}
