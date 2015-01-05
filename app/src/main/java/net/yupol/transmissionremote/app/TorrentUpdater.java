package net.yupol.transmissionremote.app;

import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.GetTorrentsRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TorrentUpdater {

    private static final String TAG = TorrentUpdater.class.getSimpleName();

    private volatile int timeout;
    private TransportManager transportManager;
    private TorrentUpdateListener listener;
    private UpdaterThread updaterThread;


    public TorrentUpdater(TransportManager transportManager, TorrentUpdateListener listener, int timeout) {
        this.transportManager = transportManager;
        this.listener = listener;
        this.timeout = timeout;
    }

    /**
     * Sets requests timeout.
     * @param timeout time in seconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void start() {
        updaterThread = new UpdaterThread();
        updaterThread.start();
    }

    public void pause() {

    }

    public void resume() {

    }

    public void stop() {
        updaterThread.interrupt();
    }

    private class UpdaterThread extends Thread {

        @Override
        public void run() {
            while (!interrupted()) {
                sendRequest();
                try {
                    TimeUnit.SECONDS.sleep(timeout);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        private void sendRequest() {
            final GetTorrentsRequest request = new GetTorrentsRequest();
            transportManager.doRequest(request, new RequestListener<Torrents>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.d(TAG, "GetTorrentsRequest failed. SC: " + request.getResponseStatusCode());

                }

                @Override
                public void onRequestSuccess(Torrents torrents) {
                    Log.d(TAG, "Torrents: " + torrents);
                    listener.onTorrentUpdate(torrents);
                }
            });
        }
    }

    public static interface TorrentUpdateListener {
        public void onTorrentUpdate(List<Torrent> torrents);
    }
}
