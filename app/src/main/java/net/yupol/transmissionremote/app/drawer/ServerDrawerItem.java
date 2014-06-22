package net.yupol.transmissionremote.app.drawer;

import net.yupol.transmissionremote.app.server.Server;

public class ServerDrawerItem extends DrawerItem {

    private Server server;

    public ServerDrawerItem(Server server) {
        super(server.getName());
        this.server = server;
    }

    public Server getServer() {
        return server;
    }
}
