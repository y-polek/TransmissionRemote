package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

import com.google.common.base.Predicate;

import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.model.json.Torrent;

public class FilterDrawerItem extends DrawerItem {

    private Predicate<Torrent> filter;
    private TransmissionRemote app;

    public FilterDrawerItem(int textResId, Context context, Predicate<Torrent> filter) {
        super(textResId, context);
        this.filter = filter;
        this.app = (TransmissionRemote) context.getApplicationContext();
    }

    @Override
    public void itemSelected() {
        app.setFilter(filter);
    }

    public Predicate<Torrent> getFilter() {
        return filter;
    }
}
