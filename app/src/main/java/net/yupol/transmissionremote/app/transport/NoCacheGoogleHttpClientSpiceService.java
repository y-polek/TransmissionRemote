package net.yupol.transmissionremote.app.transport;

import android.app.Application;
import android.app.Notification;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.octo.android.robospice.GoogleHttpClientSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.googlehttpclient.json.JsonObjectPersister;

import net.yupol.transmissionremote.app.model.json.Torrents;

public class NoCacheGoogleHttpClientSpiceService extends GoogleHttpClientSpiceService {
    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new JsonObjectPersister<>(application, new JacksonFactory(), Torrents.class));
        return cacheManager;
    }

    @Override
    public Notification createDefaultNotification() {
        return null;
    }
}
