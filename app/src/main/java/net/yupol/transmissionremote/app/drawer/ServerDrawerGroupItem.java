package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.util.Log;

import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.server.Server;

import java.util.List;

public class ServerDrawerGroupItem extends DrawerGroupItem {

    private static final String TAG = ServerDrawerGroupItem.class.getSimpleName();

    private NewServerDrawerItem newServerDrawerItem;

    public ServerDrawerGroupItem(int id, int textResId, Context context, NewServerDrawerItem newServerItem) {
        super(id, textResId, context, newServerItem);
        this.newServerDrawerItem = newServerItem;

        TransmissionRemote app = (TransmissionRemote) context.getApplicationContext();
        List<Server> servers = app.getServers();
        Server activeServer = app.getActiveServer();
        for (Server server : servers) {
            ServerDrawerItem item = new ServerDrawerItem(server, context);
            if (server.equals(activeServer)) activateServerItem(item);
            addItem(item, getItems().size() - 1);
        }
    }

    @Override
    public void childItemSelected(DrawerItem selectedItem) {
        activateServerItem(selectedItem);
    }

    public void activateServerItem(DrawerItem item) {
        Log.d(TAG, "activateServerItem: " + item);
        List<DrawerItem> allItems = getItems();

        // Skip 'Add new server' item
        if (item == newServerDrawerItem) return;

        for (DrawerItem i : allItems) {
            i.setActive(false);
        }
        item.setActive(true);
    }
}
