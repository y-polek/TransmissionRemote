package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yupol.transmissionremote.app.model.json.Torrent;

public abstract class BasePageFragment extends Fragment {

    private static final String KEY_TORRENT = "key_torrent";

    private Torrent torrent;

    public void setTorrent(Torrent torrent) {
        this.torrent = torrent;
    }

    protected Torrent getTorrent() {
        return torrent;
    }

    public abstract int getPageTitleRes();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(KEY_TORRENT, torrent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            torrent = savedInstanceState.getParcelable(KEY_TORRENT);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
