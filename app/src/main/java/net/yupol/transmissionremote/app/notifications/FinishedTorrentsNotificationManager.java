package net.yupol.transmissionremote.app.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

import net.yupol.transmissionremote.app.MainActivity;
import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.preferences.NotificationsPreferencesActivity;
import net.yupol.transmissionremote.app.preferences.PreferencesActivity;
import net.yupol.transmissionremote.app.server.Server;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;

public class FinishedTorrentsNotificationManager {

    private static final int NOTIFICATION_ID_TORRENT_FINISHED = 1001;

    private final Context context;
    private final TransmissionRemote app;
    private final NotificationManager notificationManager;
    private final FinishedTorrentsDetector finishedTorrentsDetector;

    public FinishedTorrentsNotificationManager(Context context) {
        this.context = context;
        app = TransmissionRemote.getApplication(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        finishedTorrentsDetector = new FinishedTorrentsDetector();
    }

    public void checkForFinishedTorrents(Server server, List<Torrent> torrents) {
        Collection<Torrent> torrentsToNotify = finishedTorrentsDetector.filterFinishedTorrentsToNotify(torrents, server);
        if (!torrentsToNotify.isEmpty()) {
            showFinishedNotification(torrentsToNotify);
        }

        long lastFinishedDate = finishedTorrentsDetector.findLastFinishedDate(torrents);
        if (lastFinishedDate > server.getLastUpdateDate()) {
            server.setLastUpdateDate(lastFinishedDate);
            app.persistServers();
        } else if (lastFinishedDate <= 0) {
            server.setLastUpdateDate(1L);
            app.persistServers();
        }
    }

    private void showFinishedNotification(Collection<Torrent> finishedTorrents) {
        final int count = finishedTorrents.size();
        String title = context.getResources().getQuantityString(R.plurals.torrents_finished, count, count);
        String text = from(finishedTorrents).limit(5).transform(new Function<Torrent, String>() {
            @Override
            public String apply(Torrent t) {
                return t.getName();
            }
        }).join(Joiner.on(", "));
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, TransmissionRemote.NOTIFICATION_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setSmallIcon(R.drawable.transmission)
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

        int flags;
        if (Build.VERSION.SDK_INT >= 31) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context,
                0,
                new Intent(context, MainActivity.class),
                flags
        );
        builder.setContentIntent(contentPendingIntent);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(new Intent(context, PreferencesActivity.class));
        stackBuilder.addNextIntent(new Intent(context, NotificationsPreferencesActivity.class));
        PendingIntent preferencesPendingIntent = stackBuilder.getPendingIntent(0, flags);
        builder.addAction(R.drawable.ic_settings_black_18dp, context.getString(R.string.notification_settings), preferencesPendingIntent);

        notificationManager.notify(NOTIFICATION_ID_TORRENT_FINISHED, builder.build());
    }
}
