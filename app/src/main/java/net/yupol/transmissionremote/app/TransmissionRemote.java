package net.yupol.transmissionremote.app;

import android.app.Application;
import android.content.Context;

import net.yupol.transmissionremote.app.server.Server;

import java.util.LinkedList;
import java.util.List;

public class TransmissionRemote extends Application {

    private List<Server> servers = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: read persisted servers
    }

    public List<Server> getServers() {
        return servers;
    }

    public void addServer(Server server) {
        servers.add(server);
        // TODO: persist server
    }

    public void removeServer(Server server) {
        servers.remove(server);
        // TODO: remove server from persisted storage
    }

    public static TransmissionRemote getApplication(Context context) {
        return (TransmissionRemote) context.getApplicationContext();
    }
}
