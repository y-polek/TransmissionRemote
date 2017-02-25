package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.model.json.TorrentInfo;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.TorrentInfoGetRequest;

import java.util.Timer;
import java.util.TimerTask;

public class TorrentInfoUpdater implements RequestListener<TorrentInfo> {

    private static final String TAG = TorrentInfoUpdater.class.getSimpleName();
    private static final String TIMER_NAME = TorrentInfoUpdater.class.getSimpleName();

    private TransportManager transportManager;
    private int torrentId;
    private final long timeoutMillis;
    private TorrentInfoGetRequest request;
    private Timer timer;
    private OnTorrentInfoUpdatedListener listener;

    public TorrentInfoUpdater(TransportManager transportManager, int torrentId, long timeoutMillis) {
        this.transportManager = transportManager;
        this.torrentId = torrentId;
        this.timeoutMillis = timeoutMillis;
    }

    public void start(OnTorrentInfoUpdatedListener listener) {
        this.listener = listener;
        timer = new Timer(TIMER_NAME);
        request = new TorrentInfoGetRequest(torrentId);
        transportManager.doRequest(request, this);
    }

    public void stop() {
        this.listener = null;
        if (request == null) throw new IllegalArgumentException("TorrentInfoUpdater is not started");
        timer.cancel();
        timer = null;
        request.cancel();
    }

    public void updateNow(OnTorrentInfoUpdatedListener listener) {
        stop();
        start(listener);
    }

    @Override
    public void onRequestSuccess(TorrentInfo torrentInfo) {
        if (listener != null) listener.onTorrentInfoUpdated(torrentInfo);
        if (timer != null) scheduleNexUpdate();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Log.d(TAG, "TorrentInfo request failed", spiceException);
        if (timer != null) scheduleNexUpdate();
    }

    private void scheduleNexUpdate() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (timer != null) {
                            request = new TorrentInfoGetRequest(torrentId);
                            transportManager.doRequest(request, TorrentInfoUpdater.this);
                        }
                    }
                });
            }
        }, timeoutMillis);
    }

    public interface OnTorrentInfoUpdatedListener {
        void onTorrentInfoUpdated(TorrentInfo torrentInfo);
    }
}
