package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;

import net.yupol.transmissionremote.app.R;

public class NewServerDrawerItem extends DrawerItem {

    public NewServerDrawerItem(Context c) {
        super(R.string.add_new_server_drawer_item, c);
    }

    @Override
    public int getLeftImage() {
        return android.R.drawable.ic_menu_add;
    }
}
