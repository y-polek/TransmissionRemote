package net.yupol.transmissionremote.app.transport;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import javax.annotation.Nonnull;

public class RepeaterRequestListener<RESULT> implements RequestListener<RESULT> {

    private RequestListener<RESULT> listener;
    private RequestListener<RESULT> shuntListener;

    public RepeaterRequestListener(@Nonnull RequestListener<RESULT> listener,
                                   @Nonnull RequestListener<RESULT> shuntListener) {
        this.listener = listener;
        this.shuntListener = shuntListener;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        shuntListener.onRequestFailure(spiceException);
        listener.onRequestFailure(spiceException);
    }

    @Override
    public void onRequestSuccess(RESULT result) {
        shuntListener.onRequestSuccess(result);
        listener.onRequestSuccess(result);
    }
}
