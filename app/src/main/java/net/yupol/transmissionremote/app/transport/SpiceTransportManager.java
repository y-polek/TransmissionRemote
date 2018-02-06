package net.yupol.transmissionremote.app.transport;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import com.octo.android.robospice.retry.RetryPolicy;

import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.request.Request;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import roboguice.util.temp.Ln;

public class SpiceTransportManager extends SpiceManager implements TransportManager {

    private static final String TAG = SpiceTransportManager.class.getSimpleName();

    private static final RetryPolicy RETRY_POLICY_NO_RETRIES = new DefaultRetryPolicy(0, 0L, 0);

    private Timer timer;
    private Server currentServer;

    public SpiceTransportManager() {
        super(NoCacheGoogleHttpClientSpiceService.class);
        Ln.getConfig().setLoggingLevel(Log.ERROR);
    }

    public void setServer(Server server) {
        currentServer = server;
    }

    public <T> void doRequest(final Request<T> request, @NonNull Server server, final RequestListener<T> listener) {

        request.setServer(server);
        request.setRetryPolicy(RETRY_POLICY_NO_RETRIES);

        Log.d(TAG, "execute " + request.getClass().getSimpleName() + " sessionId: " + request.getServer().getLastSessionId());
        execute(request, new RetryPropagateRequestListener<T>(request, listener) {
            @Override
            protected void retry(Request<T> request, @Nullable RequestListener<T> listener) {
                doRequest(request, request.getServer(), listener);
            }
        });
    }

    public <T> void doRequest(Request<T> request, RequestListener<T> listener) {
        if (currentServer == null)
            throw new IllegalStateException("Trying to send request while there is no active server");
        doRequest(request, currentServer, listener);
    }

    @Override
    public <T> void doRequest(final Request<T> request, final RequestListener<T> listener, long delay) {
        if (timer == null) throw new IllegalStateException("doRequest called while SpiceTransportManager is stopped");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (timer != null) {
                            doRequest(request, listener);
                        }
                    }
                });
            }
        }, delay);
    }

    @Override
    public void start(Context context) {
        super.start(context);
        timer = new Timer("SpiceTransportManger timer");
    }

    @Override
    public void shouldStop() {
        timer.cancel();
        timer = null;
        super.shouldStop();
    }
}
