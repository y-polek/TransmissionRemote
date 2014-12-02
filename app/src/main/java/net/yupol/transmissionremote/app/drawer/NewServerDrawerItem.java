package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;

import net.yupol.transmissionremote.app.R;

public class NewServerDrawerItem extends DrawerItem {

    private Drawable image;

    public NewServerDrawerItem(Context c) {
        super(c.getString(R.string.add_new_server_drawer_item));

        image = c.getResources().getDrawable(android.R.drawable.ic_menu_add);
    }

    @Override
    public Drawable getLeftImage() {
        return image;
    }
}
