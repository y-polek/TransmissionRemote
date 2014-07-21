package net.yupol.transmissionremote.app;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.Torrent;
import net.yupol.transmissionremote.app.transport.TransportThread;
import net.yupol.transmissionremote.app.transport.request.UpdateTorrentsRequest;
import net.yupol.transmissionremote.app.transport.response.UpdateTorrentsResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TorrentUpdater extends Handler {

    private static final String TAG = TorrentUpdater.class.getSimpleName();

    private long timeout = 1_000;
    private Server server;
    private TorrentUpdateListener listener;
    private TransportThread transportThread;
    private UpdaterThread updaterThread;


    public TorrentUpdater(Server server, TorrentUpdateListener listener) {
        this.server = server;
        this.listener = listener;
    }

    /**
     * Sets requests timeout.
     * @param timeout time in milliseconds
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void start() {
        transportThread = new TransportThread(server, this);
        transportThread.start();
        updaterThread = new UpdaterThread();
        updaterThread.start();
    }

    public void pause() {

    }

    public void resume() {

    }

    public void stop() {
        updaterThread.interrupt();
        transportThread.quit();
    }

    @Override
    public void handleMessage(Message msg) {
        if (!(msg.obj instanceof UpdateTorrentsResponse)) {
            throw new IllegalArgumentException("Response message must contain" +
                    " UpdateTorrentsResponse object in its 'obj' field");
        }

        UpdateTorrentsResponse response = (UpdateTorrentsResponse) msg.obj;
        if (response.getResult()) {
            List<Torrent> torrents = response.getTorrents();
            listener.onTorrentUpdate(torrents);
        } else {
            Log.e(TAG, "Failed to update torrents: " + response.toString());
        }
    }

    private class UpdaterThread extends Thread {

        @Override
        public void run() {
            while (!interrupted()) {
                sendRequest();
                try {
                    TimeUnit.MILLISECONDS.sleep(timeout);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        private void sendRequest() {
            Message msg = transportThread.getHandler().obtainMessage(TransportThread.REQUEST);
            msg.obj = new UpdateTorrentsRequest();
            transportThread.getHandler().sendMessage(msg);
        }
    }

    public static interface TorrentUpdateListener {
        public void onTorrentUpdate(List<Torrent> torrents);
    }
}
