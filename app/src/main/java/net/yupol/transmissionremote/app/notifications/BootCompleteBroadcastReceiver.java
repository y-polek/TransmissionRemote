package net.yupol.transmissionremote.app.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.yupol.transmissionremote.app.TransmissionRemote;

public class BootCompleteBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (((TransmissionRemote) context.getApplicationContext()).isNotificationEnabled()) {
            context.startService(new Intent(context, BackgroundUpdateService.class));
        }
    }
}
