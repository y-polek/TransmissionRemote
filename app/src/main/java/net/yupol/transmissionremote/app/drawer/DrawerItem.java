package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

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
}
