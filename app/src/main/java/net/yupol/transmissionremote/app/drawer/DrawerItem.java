package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;

public class DrawerItem {

    private String text;

    public DrawerItem(String text) {
        this.text = text;
    }

    public DrawerItem(int textResId, Context context) {
        this(context.getString(textResId));
    }

    public String getText() {
        return text;
    }

    public Drawable getImage() {
        return null;
    }
}
