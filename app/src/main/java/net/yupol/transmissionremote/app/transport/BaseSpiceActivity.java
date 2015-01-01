package net.yupol.transmissionremote.app.transport;

import android.app.Activity;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.SpiceServiceAdapter;
import com.octo.android.robospice.request.listener.SpiceServiceListener;

import net.yupol.transmissionremote.app.transport.request.Request;

public class BaseSpiceActivity extends Activity {
    private final SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);
    private final SpiceServiceListener spiceServiceListener = new SpiceServiceAdapter() {
        @Override
        public void onRequestSucceeded(CachedSpiceRequest<?> request, RequestProcessingContext requestProcessingContext) {

        }
    };

    {
        spiceManager.addSpiceServiceListener(spiceServiceListener);
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    public SpiceManager getSpiceManager() {
        return spiceManager;
    }

    public <T> void doRequest(final Request<T> request, RequestListener<T> listener) {
        spiceManager.execute(request, new RepeaterRequestListener<T>(listener, new RequestListener<T>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                
            }

            @Override
            public void onRequestSuccess(T t) {

            }
        }));
    }
}
