package net.yupol.transmissionremote.app.transport.request;

public class ReannounceTorrentRequest extends TorrentActionRequest {

    public ReannounceTorrentRequest(int... torrentIds) {
        super("torrent-reannounce", torrentIds);
    }
}
