package net.yupol.transmissionremote.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.di.ApplicationComponent;
import net.yupol.transmissionremote.app.di.ApplicationModule;
import net.yupol.transmissionremote.app.di.DaggerApplicationComponent;
import net.yupol.transmissionremote.app.di.DaggerTransportComponent;
import net.yupol.transmissionremote.app.di.TransportComponent;
import net.yupol.transmissionremote.app.di.TransportModule;
import net.yupol.transmissionremote.app.filtering.Filter;
import net.yupol.transmissionremote.app.filtering.Filters;
import net.yupol.transmissionremote.app.notifications.BackgroundUpdateJob;
import net.yupol.transmissionremote.app.notifications.BackgroundUpdater;
import net.yupol.transmissionremote.app.sorting.SortOrder;
import net.yupol.transmissionremote.app.sorting.SortedBy;
import net.yupol.transmissionremote.app.utils.ThemeUtils;
import net.yupol.transmissionremote.model.Server;
import net.yupol.transmissionremote.model.json.Torrent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import io.fabric.sdk.android.Fabric;

public class TransmissionRemote extends MultiDexApplication implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String SHARED_PREFS_NAME = "transmission_remote_shared_prefs";
    private static final String KEY_SERVERS = "key_servers";
    private static final String KEY_ACTIVE_SERVER = "key_active_server";
    private static final String KEY_FILTER = "key_filter";
    private static final String KEY_SORTED_BY = "key_sorted_by";
    private static final String KEY_SORT_ORDER = "key_sort_order";

    public static final String NOTIFICATION_CHANNEL_ID = "finished_torrents_notification_channel_id";

    private static final Filter[] ALL_FILTERS = {
            Filters.ALL,
            Filters.ACTIVE,
            Filters.DOWNLOADING,
            Filters.SEEDING,
            Filters.PAUSED,
            Filters.DOWNLOAD_COMPLETED,
            Filters.FINISHED
    };

    private ApplicationComponent applicationComponent;
    private TransportComponent transportComponent;

    private static TransmissionRemote instance;

    private List<Server> servers = new LinkedList<>();
    private Server activeServer;
    private boolean speedLimitEnabled;

    private List<OnSpeedLimitChangedListener> speedLimitChangedListeners = new LinkedList<>();
    private Collection<Torrent> torrents = Collections.emptyList();

    private List<OnTorrentsUpdatedListener> torrentsUpdatedListeners = new LinkedList<>();
    private Filter activeFilter = Filters.ALL;
    private List<OnFilterSelectedListener> filterSelectedListeners = new LinkedList<>();

    private SortedBy sortedBy = SortedBy.NAME;
    private SortOrder sortOrder = SortOrder.ASCENDING;
    private List<OnSortingChangedListener> sortingChangedListeners = new LinkedList<>();

    private String defaultDownloadDir;

    private Map<Server, Boolean> speedLimitsCache = new WeakHashMap<>();
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        setupCrashlytics();

        AppCompatDelegate.setDefaultNightMode(ThemeUtils.isInNightMode(this) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        instance = this;
        restore();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        JobManager.create(this)
                .addJobCreator(new BackgroundUpdateJob.Creator());


        if (isNotificationEnabled()) {
            BackgroundUpdater.start(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
    }

    private void setupCrashlytics() {
        Crashlytics crashlytics = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder()
                        .disabled(BuildConfig.DEBUG)
                        .build())
                .build();
        Fabric.with(this, crashlytics);
    }

    public TransportComponent getTransportComponent() {
        return transportComponent;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.torrent_finished_notification_enabled_key))) {
            if (isNotificationEnabled()) {
                BackgroundUpdater.start(this);
            } else {
                BackgroundUpdater.stop();
                for (Server server : servers) {
                    server.setLastUpdateDate(0);
                }
                persistServers();
            }
        } else if (key.equals(getString(R.string.background_update_only_unmetered_wifi_key))) {
            BackgroundUpdater.restart(this);
        }
    }

    public static TransmissionRemote getApplication(Context context) {
        return (TransmissionRemote) context.getApplicationContext();
    }

    public static TransmissionRemote getInstance() {
        return instance;
    }

    public List<Server> getServers() {
        return servers;
    }

    @Nullable
    public Server getServerById(@NonNull final String id) {
        for (Server server : servers) {
            if (id.equals(server.getId())) return server;
        }
        return null;
    }

    public void addServer(Server server) {
        servers.add(server);
        persistServers();

        if (isNotificationEnabled()) {
            BackgroundUpdater.start(this);
        }
    }

    public void removeServer(Server server) {
        servers.remove(server);
        if (server.equals(getActiveServer())) {
            setActiveServer(!servers.isEmpty() ? servers.get(0) : null);
        }
        persistServers();
    }

    public void updateServer(Server server) {
        persistServers();
    }

    public Server getActiveServer() {
        return activeServer;
    }

    public void setActiveServer(Server server) {
        activeServer = server;
        persistActiveServer();
        setSpeedLimitEnabled(speedLimitsCache.containsKey(server) ? speedLimitsCache.get(server) : false);

        transportComponent = DaggerTransportComponent.builder()
                .applicationComponent(applicationComponent)
                .transportModule(new TransportModule(server))
                .build();
    }

    public int getUpdateInterval() {
        return Integer.parseInt(sharedPreferences.getString(getString(R.string.update_interval_key),
                getString(R.string.update_interval_default_value)));
    }

    public boolean isNotificationEnabled() {
        return sharedPreferences.getBoolean(getString(R.string.torrent_finished_notification_enabled_key),
                Boolean.parseBoolean(getString(R.string.torrent_finished_notification_enabled_default_value)));
    }

    public boolean isNotificationVibroEnabled() {
        return sharedPreferences.getBoolean(getString(R.string.torrent_finished_notification_vibrate_key), false);
    }

    @Nullable
    public Uri getNotificationSound() {
        String uri = sharedPreferences.getString(getString(R.string.torrent_finished_notification_sound_key), "");
        return uri.isEmpty() ? null : Uri.parse(uri);
    }

    public boolean isFreeSpaceCheckDisabled() {
        return sharedPreferences.getBoolean(getString(R.string.disable_free_space_check_key), false);
    }

    public void setSpeedLimitEnabled(boolean isEnabled) {
        speedLimitEnabled = isEnabled;
        speedLimitsCache.put(activeServer, isEnabled);
        for (OnSpeedLimitChangedListener l : speedLimitChangedListeners) {
            l.speedLimitEnabledChanged(speedLimitEnabled);
        }
    }

    public boolean isSpeedLimitEnabled() {
        return speedLimitEnabled;
    }

    public void addOnSpeedLimitEnabledChangedListener(@Nonnull OnSpeedLimitChangedListener listener) {
        if (!speedLimitChangedListeners.contains(listener)) {
            speedLimitChangedListeners.add(listener);
        }
    }

    public void removeOnSpeedLimitEnabledChangedListener(@Nonnull OnSpeedLimitChangedListener listener) {
        speedLimitChangedListeners.remove(listener);
    }

    public void setTorrents(Collection<Torrent> torrents) {
        this.torrents = torrents;
        for (OnTorrentsUpdatedListener listener : torrentsUpdatedListeners) {
            listener.torrentsUpdated(torrents);
        }
    }

    public Collection<Torrent> getTorrents() {
        return torrents;
    }

    public void addTorrentsUpdatedListener(OnTorrentsUpdatedListener listener) {
        if (!torrentsUpdatedListeners.contains(listener)) {
            torrentsUpdatedListeners.add(listener);
        }
    }

    public void removeTorrentsUpdatedListener(OnTorrentsUpdatedListener listener) {
        torrentsUpdatedListeners.remove(listener);
    }

    public void setActiveFilter(@Nonnull Filter activeFilter) {
        this.activeFilter = activeFilter;
        for (OnFilterSelectedListener listener : filterSelectedListeners) {
            listener.filterSelected(activeFilter);
        }
    }

    public Filter[] getAllFilters() {
        return ALL_FILTERS;
    }

    public Filter getActiveFilter() {
        return activeFilter;
    }

    public void addOnFilterSetListener(@Nonnull OnFilterSelectedListener listener) {
        if (!filterSelectedListeners.contains(listener)) {
            filterSelectedListeners.add(listener);
        }
    }

    public void removeOnFilterSelectedListener(@Nonnull OnFilterSelectedListener listener) {
        filterSelectedListeners.remove(listener);
    }

    public void setSorting(@NonNull SortedBy sortedBy, @NonNull SortOrder sortOrder) {
        this.sortedBy = sortedBy;
        this.sortOrder = sortOrder;

        Comparator<Torrent> comparator = getSortComparator();
        for (OnSortingChangedListener listener : sortingChangedListeners) {
            listener.onSortingChanged(comparator);
        }
    }

    public SortedBy getSortedBy() {
        return sortedBy;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public Comparator<Torrent> getSortComparator() {
        return sortOrder.comparator(sortedBy.getComparator());
    }

    public void addOnSortingChangedListeners(@NonNull OnSortingChangedListener listener) {
        if (!sortingChangedListeners.contains(listener)) {
            sortingChangedListeners.add(listener);
        }
    }

    public void removeOnSortingChangedListener(@NonNull OnSortingChangedListener listener) {
        sortingChangedListeners.remove(listener);
    }

    public void setDefaultDownloadDir(String dir) {
        defaultDownloadDir = dir;
    }

    public String getDefaultDownloadDir() {
        return defaultDownloadDir;
    }

    public void persist() {
        persistServers();
        persistFilter();
        persistSorting();
    }

    private void restore() {
        restoreServers();
        restoreFilter();
        restoreSorting();
    }

    public void persistServers() {
        Set<String> serversInJson = FluentIterable.from(servers).transform(new Function<Server, String>() {
            @Override
            public String apply(@NonNull Server server) {
                return server.toJson();
            }
        }).toSet();

        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(KEY_SERVERS, serversInJson);
        editor.apply();
    }

    private void restoreServers() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        Set<String> serversInJson = sp.getStringSet(KEY_SERVERS, Collections.<String>emptySet());
        servers.addAll(FluentIterable.from(serversInJson).transform(new Function<String, Server>() {
            @Override public Server apply(@NonNull String serverInJson) {
                return Server.fromJson(serverInJson);
            }
        }).filter(Predicates.notNull()).toList());

        String activeServerInJson = sp.getString(KEY_ACTIVE_SERVER, null);
        if (activeServerInJson != null) {
            final Server persistedActiveServer = Server.fromJson(activeServerInJson);
            // active server should point to object in all servers list
            activeServer = FluentIterable.from(servers).firstMatch(new Predicate<Server>() {
                @Override public boolean apply(@NonNull Server server) {
                    return server.equals(persistedActiveServer);
                }
            }).orNull();
        } else {
            activeServer = null;
        }
        if (activeServer == null && !servers.isEmpty()) {
            activeServer = servers.get(0);
        }
    }

    private void persistActiveServer() {
        String serverInJson = activeServer != null ? activeServer.toJson() : null;

        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ACTIVE_SERVER, serverInJson);
        editor.apply();
    }

    private void persistFilter() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_FILTER, Arrays.asList(ALL_FILTERS).indexOf(activeFilter));
        editor.apply();
    }

    private void restoreFilter() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        int filterIdx = sp.getInt(KEY_FILTER, 0);
        activeFilter = ALL_FILTERS[filterIdx];
    }

    private void persistSorting() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_SORTED_BY, sortedBy.ordinal());
        editor.putInt(KEY_SORT_ORDER, sortOrder.ordinal());
        editor.apply();
    }

    private void restoreSorting() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        sortedBy = SortedBy.values()[sp.getInt(KEY_SORTED_BY, 0)];
        sortOrder = SortOrder.values()[sp.getInt(KEY_SORT_ORDER, 0)];
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);
    }

    public interface OnSpeedLimitChangedListener {
        void speedLimitEnabledChanged(boolean isEnabled);
    }
    public interface OnTorrentsUpdatedListener {
        void torrentsUpdated(Collection<Torrent> torrents);
    }
    public interface OnFilterSelectedListener {
        void filterSelected(Filter filter);
    }

    public interface OnSortingChangedListener {
        void onSortingChanged(Comparator<Torrent> comparator);
    }
}
