package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.TorrentActionRequest;

public class OpenTorrentDrawerItem extends DrawerItem {

    private TransportManager transportManager;

    public OpenTorrentDrawerItem(Context context, TransportManager transportManager) {
        super(R.string.drawer_actions_open_torrent, context);

    }

    @Override
    public void itemSelected() {

    }
}
