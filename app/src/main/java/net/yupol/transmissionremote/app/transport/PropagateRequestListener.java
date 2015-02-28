package net.yupol.transmissionremote.app.transport;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import javax.annotation.Nonnull;

public abstract class PropagateRequestListener<RESULT> implements RequestListener<RESULT> {

    private RequestListener<RESULT> listener;

    public PropagateRequestListener(@Nonnull RequestListener<RESULT> listener) {
        this.listener = listener;
    }

    @Override
    public final void onRequestFailure(SpiceException spiceException) {
        boolean propagate = onFailure(spiceException);
        if (propagate) {
            listener.onRequestFailure(spiceException);
        }
    }

    @Override
    public final void onRequestSuccess(RESULT result) {
        boolean propagate = onSuccess(result);
        if (propagate) {
            listener.onRequestSuccess(result);
        }
    }

    /**
     * Called when request failure notification received.
     * Depending on return value {@code listener} is notified or not.
     * @param spiceException exception with failure information
     * @return whether {@code listener} should be notified
     */
    protected abstract boolean onFailure(SpiceException spiceException);

    /**
     * Called when request success notification received.
     * Depending on return value {@code listener} is notified or not.
     * @param result request result
     * @return whether {@code listener} should be notified
     */
    protected abstract boolean onSuccess(RESULT result);
}
