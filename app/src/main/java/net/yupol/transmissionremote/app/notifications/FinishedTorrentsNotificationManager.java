package net.yupol.transmissionremote.app.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

import net.yupol.transmissionremote.app.MainActivity;
import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.server.ServersRepository;
import net.yupol.transmissionremote.model.json.Torrent;
import net.yupol.transmissionremote.app.preferences.NotificationsPreferencesActivity;
import net.yupol.transmissionremote.app.preferences.PreferencesActivity;
import net.yupol.transmissionremote.model.Server;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import static com.google.common.collect.FluentIterable.from;

public class FinishedTorrentsNotificationManager {

    private static final int NOTIFICATION_ID_TORRENT_FINISHED = 1001;

    private final TransmissionRemote app;
    private final ServersRepository serversRepository;
    private final NotificationManager notificationManager;
    private final FinishedTorrentsDetector finishedTorrentsDetector;

    @Inject
    public FinishedTorrentsNotificationManager(TransmissionRemote app, ServersRepository serversRepository) {
        this.app = app;
        this.serversRepository = serversRepository;
        notificationManager = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
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
        } else if (lastFinishedDate <= 0) {
            server.setLastUpdateDate(1L);
        }
        serversRepository.persistServers();
    }

    private void showFinishedNotification(Collection<Torrent> finishedTorrents) {
        final int count = finishedTorrents.size();
        String title = app.getResources().getQuantityString(R.plurals.torrents_finished, count, count);
        String text = from(finishedTorrents).limit(5).transform(new Function<Torrent, String>() {
            @Override
            public String apply(@NonNull Torrent t) {
                return t.getName();
            }
        }).join(Joiner.on(", "));
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(app, TransmissionRemote.NOTIFICATION_CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(app);
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

        PendingIntent contentPendingIntent = PendingIntent.getActivity(app, 0,
                new Intent(app, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentPendingIntent);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(app);
        stackBuilder.addNextIntentWithParentStack(new Intent(app, PreferencesActivity.class));
        stackBuilder.addNextIntent(new Intent(app, NotificationsPreferencesActivity.class));
        PendingIntent preferencesPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_settings_black_18dp, app.getString(R.string.notification_settings), preferencesPendingIntent);

        notificationManager.notify(NOTIFICATION_ID_TORRENT_FINISHED, builder.build());
    }
}
