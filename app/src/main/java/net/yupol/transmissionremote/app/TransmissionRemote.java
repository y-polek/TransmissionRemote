package net.yupol.transmissionremote.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.server.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TransmissionRemote extends Application {

    private static final String SHARED_PREFS_NAME = "transmission_remote_shared_prefs";
    private static final String KEY_SERVERS = "key_servers";
    private static final String KEY_ACTIVE_SERVER = "key_active_server";

    private List<Server> servers = new LinkedList<>();
    private Server activeServer;

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
            } catch (JSONException e) {
                activeServer = null;
            }
        } else {
            activeServer = null;
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
        return servers.isEmpty() ? null : servers.get(0);
    }

    public void setActiveServer(Server server) {
        activeServer = server;
        persistActiveServer();
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
}