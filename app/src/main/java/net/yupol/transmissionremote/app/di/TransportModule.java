package net.yupol.transmissionremote.app.di;

import android.content.Context;

import net.yupol.transmissionremote.model.Server;
import net.yupol.transmissionremote.transport.ConnectivityInterceptor;
import net.yupol.transmissionremote.transport.Transport;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;

@Module
public class TransportModule {

    private static final String CONNECTIVITY_INTERCEPTOR_NAME = "connectivity_interceptor";

    private Server server;

    public TransportModule(Server server) {
        this.server = server;
    }

    @Provides @ServerScope
    public Transport provideTransport(@Named(CONNECTIVITY_INTERCEPTOR_NAME) Interceptor interceptor) {
        return new Transport(server, interceptor);
    }

    @Provides @ServerScope
    @Named(CONNECTIVITY_INTERCEPTOR_NAME)
    public Interceptor connectivityInterceptor(Context context) {
        return new ConnectivityInterceptor(context);
    }
}
