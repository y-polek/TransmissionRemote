package net.yupol.transmissionremote.app.di;

import android.content.Context;
import android.support.annotation.NonNull;

import net.yupol.transmissionremote.app.TransmissionRemote;

public class Injector {

    public static TransportComponent transportComponent(@NonNull Context context) {
        return ((TransmissionRemote) context.getApplicationContext()).getTransportComponent();
    }
}
