package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.transport.request.TorrentGetRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TorrentUpdater {

    private static final String TAG = TorrentUpdater.class.getSimpleName();

    private volatile int timeout;
    private TransportManager transportManager;
    private TorrentUpdateListener listener;
    private UpdaterThread updaterThread;
    private TorrentGetRequest currentRequest;

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
            updaterThread.cancel();
            updaterThread = null;
        }
    }

    public void scheduleUpdate(long delay) {
        if (updaterThread == null) throw new IllegalStateException("TorrentUpdater is not started");
        updaterThread.scheduleUpdate(delay);
    }

    private class UpdaterThread extends Thread {

        private volatile Boolean responseReceived;
        private volatile boolean canceled;
        private volatile boolean scheduledUpdate;
        private volatile long scheduledUpdateDelay;

        @Override
        public void run() {
            while (!canceled) {

                if (scheduledUpdate) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(scheduledUpdateDelay);
                    } catch (InterruptedException e) {
                        if (canceled) break;
                        else continue;
                    }
                }

                if (responseReceived == null || responseReceived) {
                    responseReceived = Boolean.FALSE;
                    scheduledUpdate = false;
                    sendRequest();
                }

                if (scheduledUpdate) continue;

                try {
                    TimeUnit.SECONDS.sleep(timeout);
                } catch (InterruptedException e) {
                    if (canceled) break;
                }
            }
            if (currentRequest != null) {
                currentRequest.cancel();
            }
        }

        private void sendRequest() {
            currentRequest = new TorrentGetRequest();
            transportManager.doRequest(currentRequest, new RequestListener<Torrents>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.d(TAG, "TorrentGetRequest failed. SC: " + currentRequest.getResponseStatusCode());
                    responseReceived = Boolean.TRUE;
                }

                @Override
                public void onRequestSuccess(Torrents torrents) {
                    responseReceived = Boolean.TRUE;
                    if (!canceled) {
                        listener.onTorrentUpdate(torrents);
                    }
                }
            });
        }

        public void cancel() {
            canceled = true;
            interrupt();
        }

        public void scheduleUpdate(long delay) {
            scheduledUpdate = true;
            scheduledUpdateDelay = delay;
            interrupt();
        }
    }

    public interface TorrentUpdateListener {
        void onTorrentUpdate(List<Torrent> torrents);
    }
}
