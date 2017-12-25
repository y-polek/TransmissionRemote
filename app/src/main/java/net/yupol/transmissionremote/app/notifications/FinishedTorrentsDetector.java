package net.yupol.transmissionremote.app.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.SparseBooleanArray;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.MainActivity;
import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.preferences.NotificationsPreferencesActivity;
import net.yupol.transmissionremote.app.preferences.PreferencesActivity;
import net.yupol.transmissionremote.app.server.Server;

import java.util.LinkedList;
import java.util.List;

public class FinishedTorrentsDetector {

    private static final int NOTIFICATION_ID_TORRENT_FINISHED = 1001;

    private Context context;
    private TransmissionRemote app;
    private TorrentStatusDbHelper dbHelper;
    private SQLiteDatabase db;
    private final NotificationManager notificationManager;

    public FinishedTorrentsDetector(Context context) {
        this.context = context;
        app = TransmissionRemote.getApplication(context);
        dbHelper = new TorrentStatusDbHelper(context);
        db = dbHelper.getWritableDatabase();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void checkForFinishedTorrents(Server server, List<Torrent> torrents) {
        SparseBooleanArray previousFinishedStates = readPreviousFinishedStates(server);

        dbHelper.clearServerData(db, server.getId());

        List<Torrent> finishedTorrents = new LinkedList<>();
        try {
            db.beginTransaction();
            for (Torrent torrent : torrents) {
                ContentValues values = new ContentValues();
                values.put(TorrentStatusDbHelper.Columns.SERVER_ID, server.getId());
                values.put(TorrentStatusDbHelper.Columns.TORRENT_ID, torrent.getId());
                values.put(TorrentStatusDbHelper.Columns.TORRENT_IS_FINISHED, torrent.isCompleted());
                db.insert(TorrentStatusDbHelper.TABLE_NAME_FINISHED_STATUS, null, values);

                if (torrent.isCompleted() && !previousFinishedStates.get(torrent.getId(), true)) {
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
                TorrentStatusDbHelper.Columns.TORRENT_ID,
                TorrentStatusDbHelper.Columns.TORRENT_IS_FINISHED
        };

        String whereClause = TorrentStatusDbHelper.Columns.SERVER_ID + " = ?";
        String[] whereArgs = {
                server.getId()
        };
        Cursor cursor = db.query(TorrentStatusDbHelper.TABLE_NAME_FINISHED_STATUS, projection, whereClause, whereArgs, null, null, null);
        int idColumnIdx = cursor.getColumnIndexOrThrow(TorrentStatusDbHelper.Columns.TORRENT_ID);
        int isFinishedColumnIdx = cursor.getColumnIndexOrThrow(TorrentStatusDbHelper.Columns.TORRENT_IS_FINISHED);
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
        String title = context.getResources().getQuantityString(R.plurals.torrents_finished, count, count);
        String text = FluentIterable.from(finishedTorrents).limit(5).transform(new Function<Torrent, String>() {
            @Override
            public String apply(Torrent t) {
                return t.getName();
            }
        }).join(Joiner.on(", "));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
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

        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentPendingIntent);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(new Intent(context, PreferencesActivity.class));
        stackBuilder.addNextIntent(new Intent(context, NotificationsPreferencesActivity.class));
        PendingIntent preferencesPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_settings_black_18dp, context.getString(R.string.notification_settings), preferencesPendingIntent);

        notificationManager.notify(NOTIFICATION_ID_TORRENT_FINISHED, builder.build());
    }
}
