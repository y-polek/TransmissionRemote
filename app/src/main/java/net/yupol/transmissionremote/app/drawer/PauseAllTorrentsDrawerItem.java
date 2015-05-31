package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.StopTorrentRequest;

public class PauseAllTorrentsDrawerItem extends DrawerItem {

    private static final String TAG = PauseAllTorrentsDrawerItem.class.getSimpleName();

    private TransmissionRemote app;
    private TransportManager transportManager;

    public PauseAllTorrentsDrawerItem(Context context, TransportManager transportManager) {
        super(R.string.action_pause_all_torrents, context);
        app = (TransmissionRemote) context.getApplicationContext();
        this.transportManager = transportManager;
    }

    @Override
    public void itemSelected() {

        transportManager.doRequest(new StopTorrentRequest(app.getTorrents()), null);
    }

    @Override
    public int getLeftImage() {
        return R.drawable.ic_media_pause;
    }
}
