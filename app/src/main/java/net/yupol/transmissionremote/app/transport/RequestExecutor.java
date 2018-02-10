package net.yupol.transmissionremote.app.transport;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequestFactory;
import com.octo.android.robospice.networkstate.DefaultNetworkStateChecker;
import com.octo.android.robospice.networkstate.NetworkStateChecker;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.DefaultRequestRunner;
import com.octo.android.robospice.request.RequestProcessorListener;
import com.octo.android.robospice.request.RequestProgressManager;
import com.octo.android.robospice.request.RequestRunner;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.notifier.DefaultRequestListenerNotifier;
import com.octo.android.robospice.request.notifier.SpiceServiceListenerNotifier;
import com.octo.android.robospice.retry.DefaultRetryPolicy;
import com.octo.android.robospice.retry.RetryPolicy;

import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.request.Request;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RequestExecutor {

    private static final RetryPolicy NO_RETRY_POLICY = new DefaultRetryPolicy(0, 0, 0);

    private HttpRequestFactory httpRequestFactory = AndroidHttp.newCompatibleTransport().createRequestFactory();
    private final Map<CachedSpiceRequest<?>, Set<RequestListener<?>>> requestListenersMap = new HashMap<>();
    private final RequestRunner requestRunner;

    public RequestExecutor(Context context) {
        this(context, new DefaultNetworkStateChecker());
    }

    RequestExecutor(Context context, @Nonnull NetworkStateChecker networkStateChecker) {

        RequestProgressManager requestProgressManager = new RequestProgressManager(new RequestProcessorListener() {
            @Override
            public void requestsInProgress() {
            }

            @Override
            public void allRequestComplete() {
                unregisterAllListeners();
            }
        }, new DefaultRequestListenerNotifier(), new SpiceServiceListenerNotifier());

        requestRunner = new DefaultRequestRunner(context,
                new CacheManager() {
                    @Override public <T> T saveDataToCacheAndReturnData(T data, Object cacheKey) throws CacheSavingException, CacheCreationException {
                        return data;
                    }
                },
                Executors.newSingleThreadExecutor(),
                requestProgressManager,
                networkStateChecker
        );

        requestProgressManager.setMapRequestToRequestListener(requestListenersMap);
    }

    public <T> void executeRequest(@Nonnull Request<T> request, @Nonnull Server server, @Nullable RequestListener<T> listener) {
        request.setServer(server);
        request.setRetryPolicy(NO_RETRY_POLICY);
        request.setHttpRequestFactory(httpRequestFactory);

        CachedSpiceRequest<T> cachedSpiceRequest = new CachedSpiceRequest<>(request, null, DurationInMillis.ALWAYS_EXPIRED);

        registerListener(cachedSpiceRequest, new RetryPropagateRequestListener<T>(request, listener) {
            @Override
            protected void retry(Request<T> request, @Nullable RequestListener<T> listener) {
                executeRequest(request, request.getServer(), listener);
            }
        });

        requestRunner.executeRequest(cachedSpiceRequest);
    }

    private void registerListener(CachedSpiceRequest request, @Nonnull RequestListener listener) {
        Set<RequestListener<?>> listeners = requestListenersMap.get(request);
        if (listeners == null) {
            listeners = new HashSet<>();
            requestListenersMap.put(request, listeners);
        }
        listeners.add(listener);
    }

    public void unregisterAllListeners() {
        requestListenersMap.clear();
    }
}
