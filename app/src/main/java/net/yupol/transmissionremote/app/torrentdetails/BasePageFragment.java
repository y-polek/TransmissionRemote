package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import net.yupol.transmissionremote.app.model.json.Torrent;

public abstract class BasePageFragment extends Fragment {

    private static final String ARG_TORRENT = "arg_torrent";

    public void setTorrent(Torrent torrent) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_TORRENT, torrent);
        setArguments(args);
    }

    protected Torrent getTorrent() {
        return getArguments().getParcelable(ARG_TORRENT);
    }

    public abstract int getPageTitleRes();
}
