package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

import net.yupol.transmissionremote.app.server.Server;

public class ServerDrawerItem extends DrawerItem {

    private Server server;

    public ServerDrawerItem(Server server, Context context) {
        super(server.getName(), context);
        this.server = server;
    }

    public Server getServer() {
        return server;
    }
}
