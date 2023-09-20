package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.transport.request.TorrentGetRequest;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TorrentUpdater {

    private static final String TAG = TorrentUpdater.class.getSimpleName();

    private volatile int timeout;
    @NonNull private final TransportManager transportManager;
    @NonNull private final TorrentUpdateListener listener;
    private UpdaterThread updaterThread;
    private TorrentGetRequest currentRequest;

    public TorrentUpdater(
            @NonNull TransportManager transportManager,
            @NonNull TorrentUpdateListener listener,
            int timeout
    ) {
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
            final TorrentGetRequest request = new TorrentGetRequest();
            currentRequest = request;

            transportManager.doRequest(request, new RequestListener<>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Log.d(TAG, "TorrentGetRequest failed. SC: " + request.getResponseStatusCode());
                    responseReceived = Boolean.TRUE;
                    if (spiceException instanceof NoNetworkException) {
                        listener.onNetworkError(NetworkError.NO_NETWORK, null);
                    } else {
                        Log.d(TAG, "NetworkException: " + spiceException.getMessage() + " status code: " + request.getResponseStatusCode());
                        NetworkError error = NetworkError.OTHER;
                        if (request.getResponseStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            error = NetworkError.UNAUTHORIZED;
                        }

                        String url = request.getUrl();
                        String responseBody = request.getResponseBody();
                        String errorMessage = request.getError() != null ? errorMessage(request.getError()) : null;
                        String errorText = "<p><u>" + url + "</u></p>" + (responseBody != null ? responseBody : errorMessage);
                        listener.onNetworkError(error, errorText);
                    }
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

    private static String errorMessage(@NonNull Throwable throwable) {
        if (throwable.getCause() == null) return throwable.getMessage();

        StringBuilder builder = new StringBuilder();
        builder.append("<ul>");
        while (throwable != null) {
            String message = throwable.getMessage();
            if (message != null && !message.isEmpty()) {
                builder.append("<li>").append(message).append("</li>");
            }
            throwable = throwable.getCause();
        }
        builder.append("</ul>");

        return builder.toString();
    }

    public interface TorrentUpdateListener {
        void onTorrentUpdate(List<Torrent> torrents);
        void onNetworkError(NetworkError error, @Nullable String detailedMessage);
    }
}
