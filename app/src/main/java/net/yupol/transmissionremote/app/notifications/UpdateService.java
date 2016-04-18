package net.yupol.transmissionremote.app.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.util.SparseBooleanArray;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.MainActivity;
import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.model.json.Torrents;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.SpiceTransportManager;
import net.yupol.transmissionremote.app.transport.request.TorrentGetRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.yupol.transmissionremote.app.notifications.TorrentStatusDbHelper.Columns;

public class UpdateService extends Service {

    private static final String TAG = UpdateService.class.getSimpleName();

    public static final String KEY_TORRENT_LIST = "key_torrent_list";
    public static final String KEY_SERVER = "key_server";

    private static TransmissionRemote app;

    private static final int NOTIFICATION_ID_TORRENT_FINISHED = 1001;
    private SpiceTransportManager transportManager;
    private TorrentStatusDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public void onCreate() {
        app = TransmissionRemote.getApplication(this);

        transportManager = new SpiceTransportManager();
        transportManager.start(this);

        dbHelper = new TorrentStatusDbHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!app.isNotificationEnabled() || app.getServers().isEmpty()) {
            // Service is not needed if notifications are disabled or there are no servers to fetch data from.
            // Service will be started again once new server is added.
            stopSelf();
        } else if (intent != null && intent.hasExtra(KEY_SERVER)) {
            Server server = intent.getParcelableExtra(KEY_SERVER);
            List<Torrent> torrents = intent.getParcelableArrayListExtra(KEY_TORRENT_LIST);
            checkForFinishedTorrents(server, torrents);
        } else {
            for (final Server server : app.getServers()) {
                transportManager.doRequest(new TorrentGetRequest(), server, new RequestListener<Torrents>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.e(TAG, "Failed to retrieve torrent list from " + server.getName() + "(" + server.getHost() + ")");
                    }

                    @Override
                    public void onRequestSuccess(Torrents torrents) {
                        checkForFinishedTorrents(server, torrents);
                    }
                });
            }

            scheduleNextUpdate();
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

        Intent intent = new Intent(this, UpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(app.getBackgroundUpdateInterval()),
                pendingIntent);
    }

    private void cancelNextUpdate() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, UpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    private void checkForFinishedTorrents(Server server, List<Torrent> torrents) {
        SparseBooleanArray previousFinishedStates = readPreviousFinishedStates(server);

        dbHelper.clearServerData(db, server.getHost());

        List<Torrent> finishedTorrents = new LinkedList<>();
        try {
            db.beginTransaction();
            for (Torrent torrent : torrents) {
                ContentValues values = new ContentValues();
                values.put(Columns.SERVER_IP, server.getHost());
                values.put(Columns.TORRENT_ID, torrent.getId());
                values.put(Columns.TORRENT_IS_FINISHED, torrent.isFinished());
                db.insert(TorrentStatusDbHelper.TABLE_NAME_FINISHED_STATUS, null, values);

                if (torrent.isFinished() && !previousFinishedStates.get(torrent.getId(), false)) {
                    Log.d(TAG, "Torrent finished: " + torrent.getId() + " " + torrent.getName());
                    finishedTorrents.add(torrent);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        showFinishedNotification(finishedTorrents);
    }

    private SparseBooleanArray readPreviousFinishedStates(Server server) {
        String[] projection = {
                Columns.TORRENT_ID,
                Columns.TORRENT_IS_FINISHED
        };

        String whereClause = Columns.SERVER_IP + " = ?";
        String[] whereArgs = {
                server.getHost()
        };
        Cursor cursor = db.query(TorrentStatusDbHelper.TABLE_NAME_FINISHED_STATUS, projection, whereClause, whereArgs, null, null, null);
        int idColumnIdx = cursor.getColumnIndexOrThrow(Columns.TORRENT_ID);
        int isFinishedColumnIdx = cursor.getColumnIndexOrThrow(Columns.TORRENT_IS_FINISHED);
        SparseBooleanArray previousFinishedStates = new SparseBooleanArray();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(idColumnIdx);
            boolean isFinished = cursor.getInt(isFinishedColumnIdx) != 0;
            previousFinishedStates.put(id, isFinished);
        }
        cursor.close();

        return previousFinishedStates;
    }

    private void showFinishedNotification(List<Torrent> finishedTorrents) {
        if (finishedTorrents.isEmpty()) return;

        final int count = finishedTorrents.size();
        String title = getResources().getQuantityString(R.plurals.torrents_finished, count, count);
        String text = FluentIterable.from(finishedTorrents).limit(5).transform(new Function<Torrent, String>() {
            @Override
            public String apply(Torrent t) {
                return t.getName();
            }
        }).join(Joiner.on(", "));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.transmission)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);
        Uri sound = app.getNotificationSound();
        if (sound != null) {
            builder.setSound(sound);
        }
        int defaults = Notification.DEFAULT_LIGHTS;
        if (app.isNotificationVibroEnabled()) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        builder.setDefaults(defaults);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(title);
        for (Torrent torrent : finishedTorrents) {
            inboxStyle.addLine(torrent.getName());
        }
        builder.setStyle(inboxStyle);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_TORRENT_FINISHED, builder.build());
    }
}
