package net.yupol.transmissionremote.app.transport;

import android.app.Application;
import android.app.Notification;
import android.util.Log;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.model.Server;
import net.yupol.transmissionremote.app.transport.request.Request;

import java.security.GeneralSecurityException;
import java.util.Set;

public class NoCacheGoogleHttpClientSpiceService extends GoogleHttpClientSpiceService {

    private static final String TAG = NoCacheGoogleHttpClientSpiceService.class.getSimpleName();

    private HttpRequestFactory defaultHttpRequestFactory;
    private HttpRequestFactory trustAllHttpRequestFactory;

    @Override
    public void onCreate() {
        super.onCreate();

        defaultHttpRequestFactory = httpRequestFactory;

        try {
            trustAllHttpRequestFactory = new NetHttpTransport.Builder()
                    .doNotValidateCertificate()
                    .build()
                    .createRequestFactory();
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "Error while creating HTTP request factory", e);
        }
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        return new CacheManager() {
            @Override
            public <T> T saveDataToCacheAndReturnData(T data, Object cacheKey) throws CacheSavingException, CacheCreationException {
                return data;
            }
        };
    }

    @Override
    public Notification createDefaultNotification() {
        return null;
    }

    @Override
    public void addRequest(CachedSpiceRequest<?> request, Set<RequestListener<?>> listRequestListener) {
        if (request.getSpiceRequest() instanceof Request) {
            Server server = ((Request) request.getSpiceRequest()).getServer();
            httpRequestFactory = server.useHttps() && server.getTrustSelfSignedSslCert()
                    ? trustAllHttpRequestFactory
                    : defaultHttpRequestFactory;
        }
        super.addRequest(request, listRequestListener);
    }
}
