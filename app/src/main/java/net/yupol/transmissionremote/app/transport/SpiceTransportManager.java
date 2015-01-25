package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.request.Request;

import org.apache.http.HttpStatus;

public class SpiceTransportManager extends SpiceManager implements TransportManager {

    private static final String TAG = SpiceTransportManager.class.getSimpleName();

    private static String sessionId = "";
    private Server server;

    public SpiceTransportManager() {
        super(NoCacheGoogleHttpClientSpiceService.class);
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public <T> void doRequest(final Request<T> request, final RequestListener<T> listener) {

        setupRequest(request);

        execute(request, new RepeaterRequestListener<T>(listener, new RequestListener<T>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (request.getResponseStatusCode() == HttpStatus.SC_CONFLICT) {
                    sessionId = request.getResponseSessionId();
                    Log.d(TAG, "new sessionId: " + sessionId);
                    doRequest(request, listener);
                }
            }

            @Override
            public void onRequestSuccess(T t) {

            }
        }));
    }

    private void setupRequest(Request<?> request) {
        if (server == null)
            throw new IllegalStateException("Trying to send request while there is no active server");
        request.setServer(server);
        request.setSessionId(sessionId);
    }
}
