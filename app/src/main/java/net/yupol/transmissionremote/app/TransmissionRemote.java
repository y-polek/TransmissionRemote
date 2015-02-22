package net.yupol.transmissionremote.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.filtering.Filter;
import net.yupol.transmissionremote.app.filtering.Filters;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.server.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

public class TransmissionRemote extends Application {

    private static final String SHARED_PREFS_NAME = "transmission_remote_shared_prefs";
    private static final String KEY_SERVERS = "key_servers";
    private static final String KEY_ACTIVE_SERVER = "key_active_server";

    private List<Server> servers = new LinkedList<>();
    private Server activeServer;
    private List<OnActiveServerChangedListener> activeServerListeners = new LinkedList<>();

    private boolean speedLimitEnabled;
    private List<OnSpeedLimitChangedListener> speedLimitChangedListeners = new LinkedList<>();

    private Collection<Torrent> torrents = Collections.emptyList();
    private List<OnTorrentsUpdatedListener> torrentsUpdatedListeners = new LinkedList<>();

    private Filter filter = Filters.ALL;
    private List<OnFilterSelectedListener> filterSelectedListeners = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        Set<String> serversInJson = sp.getStringSet(KEY_SERVERS, Collections.<String>emptySet());
        servers.addAll(FluentIterable.from(serversInJson).transform(new Function<String, Server>() {
            @Override
            public Server apply(String serverInJson) {
                try {
                    return Server.fromJson(new JSONObject(serverInJson));
                } catch (JSONException e) {
                    return null;
                }
            }
        }).filter(Predicates.notNull()).toList());

        String activeServerInJson = sp.getString(KEY_ACTIVE_SERVER, null);
        if (activeServerInJson != null) {
            try {
                activeServer = Server.fromJson(new JSONObject(activeServerInJson));
                fireActiveServerChangedEvent();
            } catch (JSONException e) {
                activeServer = null;
            }
        } else {
            activeServer = null;
        }
        if (activeServer == null && !servers.isEmpty()) {
            activeServer = servers.get(0);
        }
    }

    public static TransmissionRemote getApplication(Context context) {
        return (TransmissionRemote) context.getApplicationContext();
    }

    public List<Server> getServers() {
        return servers;
    }

    public void addServer(Server server) {
        servers.add(server);
        persistServerList();
    }

    public void removeServer(Server server) {
        servers.remove(server);
        persistServerList();
    }

    public Server getActiveServer() {
        return activeServer;
    }

    public void setActiveServer(Server server) {
        activeServer = server;
        persistActiveServer();
        fireActiveServerChangedEvent();
    }

    public void addOnActiveServerChangedListener(@Nonnull OnActiveServerChangedListener listener) {
        if (!activeServerListeners.contains(listener)) {
            activeServerListeners.add(listener);
        }
    }

    public void removeOnActiveServerChangedListener(@Nonnull OnActiveServerChangedListener listener) {
        activeServerListeners.remove(listener);
    }

    private void fireActiveServerChangedEvent() {
        for (OnActiveServerChangedListener listener : activeServerListeners) {
            listener.serverChanged(activeServer);
        }
    }

    public int getUpdateInterval() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt(sp.getString(getString(R.string.update_interval_key),
                getString(R.string.update_interval_default_value)));
    }

    public void setSpeedLimitEnabled(boolean isEnabled) {
        speedLimitEnabled = isEnabled;
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

    public void setFilter(@Nonnull Filter filter) {
        this.filter = filter;
        for (OnFilterSelectedListener listener : filterSelectedListeners) {
            listener.filterSelected(filter);
        }
    }

    public Filter getFilter() {
        return filter;
    }

    public void addOnFilterSetListener(@Nonnull OnFilterSelectedListener listener) {
        if (!filterSelectedListeners.contains(listener)) {
            filterSelectedListeners.add(listener);
        }
    }

    public void removeOnFilterSelectedListener(@Nonnull OnFilterSelectedListener listener) {
        filterSelectedListeners.remove(listener);
    }

    private void persistServerList() {
        Set<String> serversInJson = FluentIterable.from(servers).transform(new Function<Server, String>() {
            @Override
            public String apply(Server server) {
                return server.toJson().toString();
            }
        }).toSet();

        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(KEY_SERVERS, serversInJson);
        editor.commit();
    }

    private void persistActiveServer() {
        String serverInJson = activeServer != null ? activeServer.toJson().toString() : null;

        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_ACTIVE_SERVER, serverInJson);
        editor.commit();
    }

    public static interface OnActiveServerChangedListener {
        public void serverChanged(Server newServer);
    }

    public static interface OnSpeedLimitChangedListener {
        public void speedLimitEnabledChanged(boolean isEnabled);
    }

    public static interface OnTorrentsUpdatedListener {
        public void torrentsUpdated(Collection<Torrent> torrents);
    }

    public static interface OnFilterSelectedListener {
        public void filterSelected(Filter filter);
    }
}
