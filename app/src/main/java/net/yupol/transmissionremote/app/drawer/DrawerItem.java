package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

public class DrawerItem {

    private int textResId;

    public DrawerItem(int textResId) {
        this.textResId = textResId;
    }

    public int getTextResource() {
        return textResId;
    }

    public String getText(Context context) {
        return context.getText(textResId).toString();
    }
}
