package net.yupol.transmissionremote.app.drawer;

import android.content.Context;

import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.filtering.Filter;
import net.yupol.transmissionremote.app.model.json.Torrent;

import java.util.Collection;

import javax.annotation.Nullable;

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

    public void setActiveFilter(Filter filter) {
        for (DrawerItem item : getItems()) {
            if (((FilterDrawerItem) item).getFilter().equals(filter)) {
                childItemSelected(item);
            }
        }
    }

    public void updateCount(@Nullable Collection<Torrent> torrents) {
        for (DrawerItem item : getItems()) {
            FilterDrawerItem filterItem = (FilterDrawerItem) item;
            int count =torrents != null ?
                    FluentIterable.from(torrents).filter(filterItem.getFilter()).size() : -1;
            filterItem.setCount(count);
        }
    }
}
