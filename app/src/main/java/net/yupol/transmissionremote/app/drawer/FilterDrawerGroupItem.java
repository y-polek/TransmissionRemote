package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

import com.google.common.base.Predicate;

import net.yupol.transmissionremote.app.model.json.Torrent;

public class FilterDrawerGroupItem extends DrawerGroupItem {

    public FilterDrawerGroupItem(int id, int textResId, Context context, FilterDrawerItem... items) {
        super(id, textResId, context, items);
    }

    @Override
    public void childItemSelected(DrawerItem selectedItem) {
        for (DrawerItem item : getItems()) {
            item.setActive(false);
        }
        selectedItem.setActive(true);
    }

    public void setActiveFilter(Predicate<Torrent> filter) {
        for (DrawerItem item : getItems()) {
            if (((FilterDrawerItem) item).getFilter().equals(filter)) {
                childItemSelected(item);
            }
        }
    }
}
