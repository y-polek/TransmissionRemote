package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.transport.request.GetTorrentsRequest;
import net.yupol.transmissionremote.app.transport.request.Request;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TorrentUpdater {

    private static final String TAG = TorrentUpdater.class.getSimpleName();

    private volatile int timeout;
    private TransportManager transportManager;
    private TorrentUpdateListener listener;
    private UpdaterThread updaterThread;
    private Request currentRequest;

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
        if (updaterThread != null) {
            throw new IllegalStateException("TorrentUpdater thread already started");
        }
        updaterThread = new UpdaterThread();
        updaterThread.start();
    }

    public void stop() {
        if (updaterThread != null) {
            updaterThread.interrupt();
            updaterThread = null;
        }
    }

    private class UpdaterThread extends Thread {

        private Boolean responseReceived;

        @Override
        public void run() {
            while (!isInterrupted()) {
                if (responseReceived == null || responseReceived) {
                    responseReceived = false;
                    sendRequest();
                }
                try {
                    TimeUnit.SECONDS.sleep(timeout);
                } catch (InterruptedException e) {
                    break;
                }
            }
            if (currentRequest != null) {
                currentRequest.cancel();
            }
        }

        private void sendRequest() {
            currentRequest = new GetTorrentsRequest();
            transportManager.doRequest(currentRequest, new RequestListener<Torrents>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.d(TAG, "GetTorrentsRequest failed. SC: " + currentRequest.getResponseStatusCode());
                    responseReceived = Boolean.TRUE;
                }

                @Override
                public void onRequestSuccess(Torrents torrents) {
                    responseReceived = Boolean.TRUE;
                    if (!isInterrupted()) {
                        listener.onTorrentUpdate(torrents);
                    }
                }
            });
        }
    }

    public static interface TorrentUpdateListener {
        public void onTorrentUpdate(List<Torrent> torrents);
    }
}
