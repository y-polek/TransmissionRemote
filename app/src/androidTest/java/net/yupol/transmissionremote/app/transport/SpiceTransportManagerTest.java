package net.yupol.transmissionremote.app.transport;

import android.support.test.runner.AndroidJUnit4;

import com.google.api.client.http.HttpStatusCodes;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.request.Request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class SpiceTransportManagerTest {

    private static final String SESSION_ID = "fake_session_id";
    private static final String REDIRECT_LOCATION = "fake_redirect_location";

    private Server server;

    @Mock private Request<String> request;
    @Mock private RequestListener<String> requestListener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        server = new Server("Test Server", "http://localhost", 9091);
    }

    @Test
    public void testDoRequestWithConflictResponse() {
        when(request.getResponseStatusCode())
                .thenReturn(HttpStatusCodes.STATUS_CODE_CONFLICT)
                .thenReturn(HttpStatusCodes.STATUS_CODE_SERVER_ERROR);
        when(request.getResponseSessionId())
                .thenReturn(SESSION_ID);
        when(request.getServer())
                .thenReturn(server);

        SpiceTransportManager fakeTransportManager = new SpiceTransportManager() {
            @Override
            public <T> void execute(SpiceRequest<T> request, RequestListener<T> requestListener) {
                requestListener.onRequestFailure(new SpiceException("Fake Exception"));
            }
        };

        fakeTransportManager.doRequest(request, server, requestListener);

        verify(requestListener, times(1)).onRequestFailure(any(SpiceException.class));
        verifyNoMoreInteractions(requestListener);
        assertThat(server.getLastSessionId(), equalTo(SESSION_ID));
    }

    @Test
    public void testDoRequestWithRedirectResponse() {
        when(request.getResponseStatusCode())
                .thenReturn(HttpStatusCodes.STATUS_CODE_TEMPORARY_REDIRECT)
                .thenReturn(HttpStatusCodes.STATUS_CODE_SERVER_ERROR);
        when(request.getRedirectLocation())
                .thenReturn(REDIRECT_LOCATION);
        when(request.getServer())
                .thenReturn(server);

        SpiceTransportManager fakeTransportManager = new SpiceTransportManager() {
            @Override
            public <T> void execute(SpiceRequest<T> request, RequestListener<T> requestListener) {
                requestListener.onRequestFailure(new SpiceException("Fake Exception"));
            }
        };

        fakeTransportManager.doRequest(request, server, requestListener);

        verify(requestListener, times(1)).onRequestFailure(any(SpiceException.class));
        verifyNoMoreInteractions(requestListener);
        assertThat(server.getRedirectLocation(), equalTo(REDIRECT_LOCATION));
    }
}
