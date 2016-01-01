package net.yupol.transmissionremote.app.transport;

import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.model.json.PortTestResult;
import net.yupol.transmissionremote.app.transport.request.PortTestRequest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

public class PortChecker {

    private static final String TAG = PortChecker.class.getSimpleName();

    private static final int RETRY_INTERVAL = 3; // seconds

    private TransportManager tm;
    private PortCheckResultListener listener;
    private Thread thread;
    private PortTestRequest request = new PortTestRequest();

    public PortChecker(@Nonnull TransportManager tm, PortCheckResultListener resultsListener) {
        this.tm = tm;
        this.listener = resultsListener;
    }

    public void startCheck() {
        thread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    CountDownLatch latch = new CountDownLatch(1);
                    checkPort(latch);
                    try {
                        latch.await();
                        TimeUnit.SECONDS.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        thread.start();
    }

    public boolean isRunning() {
        return thread != null;
    }

    public void cancel() {
        if (thread == null) {
            throw new IllegalStateException("PortChecker cannot be canceled before being started");
        }

        thread.interrupt();
        thread = null;
        request.cancel();
    }

    private void checkPort(final CountDownLatch latch) {
        Log.d(TAG, "checkPort " + System.identityHashCode(Thread.currentThread()));
        tm.doRequest(request, new RequestListener<PortTestResult>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d(TAG, "request failure " + spiceException.getMessage());
                Throwable cause = spiceException.getCause();
                if (cause != null)
                    Log.d(TAG, "cause: " + cause.getMessage());
                latch.countDown();
            }

            @Override
            public void onRequestSuccess(PortTestResult portTestResult) {
                Log.d(TAG, "request success");
                if (listener != null) {
                    listener.onPortCheckResults(portTestResult.isOpen());
                }
                cancel();
            }
        });
    }

    public interface PortCheckResultListener {
        void onPortCheckResults(boolean isOpen);
    }
}
