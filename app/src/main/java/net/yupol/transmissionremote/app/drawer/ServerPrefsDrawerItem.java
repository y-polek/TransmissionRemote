package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

import net.yupol.transmissionremote.app.R;

public class ServerPrefsDrawerItem extends DrawerItem {

    public ServerPrefsDrawerItem(int textResId, Context context) {
        super(textResId, context);
    }

    @Override
    public int getLeftImage() {
        return R.drawable.preferences_server;
    }
}
