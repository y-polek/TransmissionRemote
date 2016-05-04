package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.TorrentInfo;

public abstract class BasePageFragment extends Fragment {

    private static final String KEY_TORRENT = "key_torrent";
    private static final String KEY_TORRENT_INFO = "key_torrent_info";

    private Torrent torrent;
    private TorrentInfo torrentInfo;

    public void setTorrent(Torrent torrent) {
        this.torrent = torrent;
    }

    public void setTorrentInfo(TorrentInfo torrentInfo) {
        this.torrentInfo = torrentInfo;
    }

    protected Torrent getTorrent() {
        return torrent;
    }

    protected TorrentInfo getTorrentInfo() {
        return torrentInfo;
    }

    public abstract int getPageTitleRes();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_TORRENT, torrent);
        outState.putParcelable(KEY_TORRENT_INFO, torrentInfo);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            torrent = savedInstanceState.getParcelable(KEY_TORRENT);
            torrentInfo = savedInstanceState.getParcelable(KEY_TORRENT_INFO);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
