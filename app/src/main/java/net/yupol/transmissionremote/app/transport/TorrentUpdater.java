package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import net.yupol.transmissionremote.model.json.Torrent;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import transport.NoNetworkException;
import transport.rpc.RpcArgs;
import transport.Transport;

public class TorrentUpdater {

    private static final String TAG = TorrentUpdater.class.getSimpleName();

    private volatile int timeout;
    private Transport transport;
    private TorrentUpdateListener listener;
    private UpdaterThread updaterThread;
    private Disposable currentRequest;

    public TorrentUpdater(Transport transport, TorrentUpdateListener listener, int timeout) {
        this.transport = transport;
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
                currentRequest.dispose();
            }
        }

        private void sendRequest() {
            transport.getApi().torrentList(RpcArgs.torrentGet())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<Torrent>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            currentRequest = d;
                        }

                        @Override
                        public void onSuccess(List<Torrent> torrents) {
                            responseReceived = Boolean.TRUE;
                            if (!canceled) {
                                listener.onTorrentUpdate(torrents);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "Can't fetch torrent list", e);
                            responseReceived = Boolean.TRUE;

                            NetworkError error = NetworkError.OTHER;
                            if (e instanceof HttpException) {
                                if (((HttpException) e).code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                    error = NetworkError.UNAUTHORIZED;
                                }
                            } else if (e instanceof NoNetworkException) {
                                error = NetworkError.NO_NETWORK;
                            }
                            listener.onNetworkError(error);
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
        void onNetworkError(NetworkError error);
    }
}
