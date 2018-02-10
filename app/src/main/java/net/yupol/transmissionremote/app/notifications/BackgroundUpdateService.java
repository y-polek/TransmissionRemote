package net.yupol.transmissionremote.app.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.SpiceTransportManager;
import net.yupol.transmissionremote.app.transport.request.TorrentGetRequest;

import java.util.concurrent.TimeUnit;

public class BackgroundUpdateService extends Service {

    private static final String TAG = BackgroundUpdateService.class.getSimpleName();

    private static TransmissionRemote app;

    private SpiceTransportManager transportManager;
    private ConnectivityManager connectivityManager;
    private FinishedTorrentsNotificationManager finishedTorrentsNotificationManager;

    @Override
    public void onCreate() {
        app = TransmissionRemote.getApplication(this);

        transportManager = new SpiceTransportManager();
        transportManager.start(this);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        finishedTorrentsNotificationManager = new FinishedTorrentsNotificationManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!app.isNotificationEnabled() || app.getServers().isEmpty()) {
            // Service is not needed if notifications are disabled or there are no servers to fetch data from.
            // Service will be started again once new server is added.
            stopSelf();
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = networkInfo != null && networkInfo.isConnected();
            if (isConnected) {
                for (final Server server : app.getServers()) {
                    transportManager.doRequest(new TorrentGetRequest(), server, new RequestListener<Torrents>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            Log.e(TAG, "Failed to retrieve torrent list from " + server.getName() + "(" + server.getHost() + ")");
                        }

                        @Override
                        public void onRequestSuccess(Torrents torrents) {
                            finishedTorrentsNotificationManager.checkForFinishedTorrents(server, torrents);
                        }
                    });
                }

                scheduleNextUpdate();
            } else {
                ConnectivityChangeReceiver.startServiceOnceConnected(this);
                stopSelf();
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        transportManager.cancelAllRequests();
        transportManager.shouldStop();

        cancelNextUpdate();
    }

    private void scheduleNextUpdate() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, BackgroundUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(app.getBackgroundUpdateInterval()),
                pendingIntent);
    }

    private void cancelNextUpdate() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, BackgroundUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    private static class ConnectivityChangeReceiver extends BroadcastReceiver {

        private static ConnectivityChangeReceiver receiver = new ConnectivityChangeReceiver();
        private static boolean isRegistered = false;

        public static void startServiceOnceConnected(Context context) {
            if (!isRegistered) {
                context.getApplicationContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                isRegistered = true;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                context.unregisterReceiver(this);
                isRegistered = false;
                context.startService(new Intent(context, BackgroundUpdateService.class));
            }
        }
    }
}
