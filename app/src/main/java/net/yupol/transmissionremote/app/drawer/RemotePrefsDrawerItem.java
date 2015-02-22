package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.content.Intent;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.preferences.RemotePreferencesActivity;

public class RemotePrefsDrawerItem extends DrawerItem {

    private Context context;

    public RemotePrefsDrawerItem(int textResId, Context context) {
        super(textResId, context);
        this.context = context;
    }

    @Override
    public void itemSelected() {
        super.itemSelected();

        context.startActivity(new Intent(context, RemotePreferencesActivity.class));
    }

    @Override
    public int getLeftImage() {
        return R.drawable.ic_menu_preferences;
    }
}
