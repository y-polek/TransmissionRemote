package net.yupol.transmissionremote.app.transport;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.request.Request;

import org.apache.http.HttpStatus;

import java.util.Timer;
import java.util.TimerTask;

public class SpiceTransportManager extends SpiceManager implements TransportManager {

    private static final String TAG = SpiceTransportManager.class.getSimpleName();

    private Timer timer;
    private Server currentServer;

    public SpiceTransportManager() {
        super(NoCacheGoogleHttpClientSpiceService.class);
    }

    public void setServer(Server server) {
        currentServer = server;
    }

    public <T> void doRequest(final Request<T> request, @NonNull Server server, final RequestListener<T> listener) {

        request.setServer(server);

        execute(request, new PropagateRequestListener<T>(listener) {
            @Override
            protected boolean onFailure(SpiceException spiceException) {
                if (request.getResponseStatusCode() == HttpStatus.SC_CONFLICT) {
                    String responseSessionId = request.getResponseSessionId();
                    request.getServer().setLastSessionId(responseSessionId);
                    Log.d(TAG, "new sessionId: " + responseSessionId);
                    doRequest(request, request.getServer(), listener);
                    return false;
                }
                return true;
            }

            @Override
            protected boolean onSuccess(T t) {
                return true;
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
