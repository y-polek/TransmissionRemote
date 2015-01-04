package net.yupol.transmissionremote.app;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.GetTorrentsRequest;
import net.yupol.transmissionremote.app.transport.response.UpdateTorrentsResponse;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TorrentUpdater extends Handler {

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

    @Override
    public void handleMessage(Message msg) {
        if (!(msg.obj instanceof UpdateTorrentsResponse)) {
            throw new IllegalArgumentException("Response message must contain" +
                    " UpdateTorrentsResponse object in its 'obj' field");
        }

        /*UpdateTorrentsResponse response = (UpdateTorrentsResponse) msg.obj;
        if (response.getResult()) {
            List<Torrent> torrents = response.getTorrents();
            listener.onTorrentUpdate(torrents);
        } else {
            Log.e(TAG, "Failed to update torrents: " + response.toString());
        }*/
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
            transportManager.doRequest(request, new RequestListener<Torrent[]>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.d(TAG, "GetTorrentsRequest failed. SC: " + request.getResponseStatusCode());

                }

                @Override
                public void onRequestSuccess(Torrent[] torrents) {
                    Log.d(TAG, "Torrents: " + Arrays.toString(torrents));
                }
            });
        }
    }

    public static interface TorrentUpdateListener {
        public void onTorrentUpdate(List<net.yupol.transmissionremote.app.transport.Torrent> torrents);
    }
}
