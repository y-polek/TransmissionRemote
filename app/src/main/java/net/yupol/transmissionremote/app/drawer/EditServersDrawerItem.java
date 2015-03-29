package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.content.Intent;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.preferences.ServersActivity;

public class EditServersDrawerItem extends DrawerItem {

    public EditServersDrawerItem(Context c) {
        super(R.string.edit_servers_drawer_item, c);
    }

    @Override
    public void itemSelected() {
        getContext().startActivity(new Intent(getContext(), ServersActivity.class));
    }

    @Override
    public int getLeftImage() {
        return R.drawable.server;
    }
}
