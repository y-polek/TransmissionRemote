package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

import net.yupol.transmissionremote.app.server.Server;

import javax.annotation.Nonnull;

public class ServerDrawerItem extends DrawerItem {

    private Server server;

    public ServerDrawerItem(@Nonnull Server server, Context context) {
        super(null, context);
        this.server = server;
    }

    @Override
    public String getText() {
        return server.getName();
    }

    public Server getServer() {
        return server;
    }
}
