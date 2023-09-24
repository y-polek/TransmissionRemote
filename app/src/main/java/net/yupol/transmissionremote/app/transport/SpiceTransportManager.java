package net.yupol.transmissionremote.app.transport;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import com.octo.android.robospice.retry.RetryPolicy;

import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.request.Request;

import javax.annotation.Nullable;

import roboguice.util.temp.Ln;

public class SpiceTransportManager extends SpiceManager implements TransportManager {

    private static final String TAG = SpiceTransportManager.class.getSimpleName();

    private static final RetryPolicy RETRY_POLICY_NO_RETRIES = new DefaultRetryPolicy(0, 0L, 0);

    private Server currentServer;

    public SpiceTransportManager() {
        super(NoCacheGoogleHttpClientSpiceService.class);
        Ln.getConfig().setLoggingLevel(Log.ERROR);
    }

    public void setServer(@Nullable Server server) {
        currentServer = server;
    }

    private <T> void doRequest(final Request<T> request, @NonNull Server server, @Nullable final RequestListener<T> listener) {
        request.setServer(server);
        request.setRetryPolicy(RETRY_POLICY_NO_RETRIES);

        Log.d(TAG, "execute " + request.getClass().getSimpleName());
        execute(request, new RetryPropagateRequestListener<>(request, listener) {
            @Override
            protected void retry(Request<T> request, @Nullable RequestListener<T> listener) {
                doRequest(request, request.getServer(), listener);
            }
        });
    }

    public <T> void doRequest(@NonNull Request<T> request, RequestListener<T> listener) {
        if (currentServer == null)
            throw new IllegalStateException("Trying to send request while there is no active server");
        doRequest(request, currentServer, listener);
    }

    @Override
    public <T> void doRequest(@NonNull final Request<T> request, final RequestListener<T> listener, long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(
                () -> doRequest(request, listener),
                delay
        );
    }
}
