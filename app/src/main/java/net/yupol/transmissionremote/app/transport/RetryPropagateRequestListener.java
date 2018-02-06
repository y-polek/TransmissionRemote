package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import com.google.api.client.http.HttpStatusCodes;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.transport.request.Request;

import java.net.HttpURLConnection;

import javax.annotation.Nullable;

public abstract class RetryPropagateRequestListener<T> extends PropagateRequestListener<T> {

    private static final String TAG = RetryPropagateRequestListener.class.getSimpleName();

    private Request<T> request;
    private RequestListener<T> listener;

    public RetryPropagateRequestListener(Request<T> request,  @Nullable RequestListener<T> listener) {
        super(listener);
        this.request = request;
        this.listener = listener;
    }

    @Override
    protected boolean onSuccess(T t) {
        return true;
    }

    @Override
    protected boolean onFailure(SpiceException spiceException) {
        int statusCode = request.getResponseStatusCode();
        if (statusCode == HttpURLConnection.HTTP_CONFLICT) {
            Log.d(TAG, "SC_CONFLICT old sessionId: " + request.getServer().getLastSessionId());
            String responseSessionId = request.getResponseSessionId();
            Log.d(TAG, "new sessionId: " + responseSessionId);
            request.getServer().setLastSessionId(responseSessionId);
            retry(request, listener);
            return false;
        } else if (HttpStatusCodes.isRedirect(statusCode)) {
            request.getServer().setRedirectLocation(request.getRedirectLocation());
            retry(request, listener);
            return false;
        }
        return true;
    }

    protected abstract void retry(Request<T> request, @Nullable RequestListener<T> listener);
}
