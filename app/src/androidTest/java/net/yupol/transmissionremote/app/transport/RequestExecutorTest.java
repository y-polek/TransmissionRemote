package net.yupol.transmissionremote.app.transport;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.request.Request;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Nonnull;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class RequestExecutorTest {

    private static final String FAKE_RESULT = "Fake Result";
    private static final Exception FAKE_EXCEPTION = new IOException("Fake Exception");

    private RequestExecutor executor;
    private Server server;

    @Mock private Request<String> mockSuccessfulRequest;
    @Mock private Request<String> mockFailedRequest;
    @Mock private RequestListener<String> mockListener;

    @Captor private ArgumentCaptor<SpiceException> spiceExceptionArgumentCaptor;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockSuccessfulRequest.loadDataFromNetwork())
                .thenReturn(FAKE_RESULT);
        when(mockSuccessfulRequest.getResultType())
                .thenReturn(String.class);

        when(mockFailedRequest.loadDataFromNetwork())
                .thenThrow(FAKE_EXCEPTION);
        when(mockFailedRequest.getResultType())
                .thenReturn(String.class);

        server = new Server("Test Server", "http://localhost", 9091);

        executor = new RequestExecutor(
                InstrumentationRegistry.getContext(),
                FakeNetworkStateChecker.INSTANCE);
    }

    @Test
    public void testExecuteRequest() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        executor.executeRequest(mockSuccessfulRequest, server, CountDownRequestListenerWrapper.wrap(mockListener, latch));

        latch.await();
        verify(mockListener).onRequestSuccess(FAKE_RESULT);
        verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void testExecuteFailedResult() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        executor.executeRequest(mockFailedRequest, server, CountDownRequestListenerWrapper.wrap(mockListener, latch));

        latch.await();
        verify(mockListener).onRequestFailure(spiceExceptionArgumentCaptor.capture());
        Assert.assertThat(spiceExceptionArgumentCaptor.getValue().getCause(),
                IsEqual.<Throwable>equalTo(FAKE_EXCEPTION));
        verifyNoMoreInteractions(mockListener);
    }

    @Test
    public void testExecuteMultipleRequests() throws Exception {
        final int n = 10;
        CountDownLatch latch = new CountDownLatch(n);

        for (int i=0; i<n; i++) {
            executor.executeRequest(mockSuccessfulRequest, server, CountDownRequestListenerWrapper.wrap(mockListener, latch));
        }

        latch.await();
        verify(mockListener, times(n)).onRequestSuccess(FAKE_RESULT);
        verifyNoMoreInteractions(mockListener);
    }

    //region Helper Classes
    private static class FakeNetworkStateChecker implements NetworkStateChecker {

        static final NetworkStateChecker INSTANCE = new FakeNetworkStateChecker();

        @Override
        public boolean isNetworkAvailable(Context context) {
            return true;
        }

        @Override
        public void checkPermissions(Context context) {

        }
    }

    private static class CountDownRequestListenerWrapper<T> implements RequestListener<T> {

        private RequestListener<T> listener;
        private CountDownLatch latch;

        CountDownRequestListenerWrapper(@Nonnull RequestListener<T> listener, CountDownLatch latch) {
            this.listener = listener;
            this.latch = latch;
        }

        @Override
        public void onRequestSuccess(T t) {
            listener.onRequestSuccess(t);
            latch.countDown();
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            listener.onRequestFailure(spiceException);
            latch.countDown();
        }

        static <T> RequestListener<T> wrap(@Nonnull RequestListener<T> listener, @Nonnull CountDownLatch latch) {
            return new CountDownRequestListenerWrapper<>(listener, latch);
        }
    }
    //endregion
}
