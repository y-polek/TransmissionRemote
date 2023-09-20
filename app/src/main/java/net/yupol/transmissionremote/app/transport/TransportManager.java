package net.yupol.transmissionremote.app.transport;

import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.transport.request.Request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TransportManager {
    <T> void doRequest(@Nonnull final Request<T> request, @Nullable RequestListener<T> listener);
    <T> void doRequest(@Nonnull final Request<T> request, @Nullable RequestListener<T> listener, long delay);
    boolean isStarted();
}
