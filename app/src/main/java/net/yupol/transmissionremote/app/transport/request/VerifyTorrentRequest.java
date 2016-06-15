package net.yupol.transmissionremote.app.transport.request;

public class VerifyTorrentRequest extends TorrentActionRequest {

    public VerifyTorrentRequest(int[] torrentIds) {
        super("torrent-verify", torrentIds);
    }
}
