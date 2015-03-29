package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.util.Log;

import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.server.Server;

import java.util.List;

public class ServerDrawerGroupItem extends DrawerGroupItem {

    private static final String TAG = ServerDrawerGroupItem.class.getSimpleName();

    private EditServersDrawerItem newServerDrawerItem;

    public ServerDrawerGroupItem(int id, int textResId, Context context, EditServersDrawerItem newServerItem) {
        super(id, textResId, context, newServerItem);
        this.newServerDrawerItem = newServerItem;
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
