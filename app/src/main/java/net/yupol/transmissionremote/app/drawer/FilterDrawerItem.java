package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.filtering.Filter;

public class FilterDrawerItem extends DrawerItem {

    private Filter filter;
    private TransmissionRemote app;
    private int count = -1;

    public FilterDrawerItem(int textResId, Context context, Filter filter) {
        super(textResId, context);
        this.filter = filter;
        this.app = (TransmissionRemote) context.getApplicationContext();
    }

    @Override
    public void itemSelected() {
        app.setActiveFilter(filter);
    }

    @Override
    public String getRightText() {
        return count >= 0 ? Integer.toString(count) : null;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
