package net.yupol.transmissionremote.app.torrentdetails;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import net.yupol.transmissionremote.model.json.TorrentInfo;
import net.yupol.transmissionremote.data.api.Transport;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TorrentInfoUpdater {

    private static final String TAG = TorrentInfoUpdater.class.getSimpleName();
    private static final String TIMER_NAME = TorrentInfoUpdater.class.getSimpleName();

    private int torrentId;
    private final long timeoutMillis;
    private Disposable request;
    private Timer timer;
    private OnTorrentInfoUpdatedListener listener;
    private Transport transport;

    public TorrentInfoUpdater(Transport transport, int torrentId, long timeoutMillis) {
        this.transport = transport;
        this.torrentId = torrentId;
        this.timeoutMillis = timeoutMillis;
    }

    public void start(final OnTorrentInfoUpdatedListener listener) {
        this.listener = listener;
        timer = new Timer(TIMER_NAME);
        loadTorrentInfoAndReschedule();
    }

    private void loadTorrentInfoAndReschedule() {
        transport.api().torrentInfo(torrentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TorrentInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        request = d;
                    }

                    @Override
                    public void onSuccess(TorrentInfo torrentInfo) {
                        if (listener != null) listener.onTorrentInfoUpdated(torrentInfo);
                        if (timer != null) scheduleNexUpdate();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "TorrentInfo request failed", e);
                        if (timer != null) scheduleNexUpdate();
                    }
                });
    }

    public void stop() {
        this.listener = null;
        if (request == null) throw new IllegalArgumentException("TorrentInfoUpdater is not started");
        timer.cancel();
        timer = null;
        request.dispose();
    }

    public void updateNow(OnTorrentInfoUpdatedListener listener) {
        stop();
        start(listener);
    }

    private void scheduleNexUpdate() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (timer != null) {
                            loadTorrentInfoAndReschedule();
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
