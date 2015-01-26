package net.yupol.transmissionremote.app.drawer;

import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.sorting.SortOrder;

import java.util.Comparator;
import java.util.List;

public class SortDrawerGroupItem extends DrawerGroupItem {

    public SortDrawerGroupItem(int id, String text, SortDrawerItem... items) {
        super(id, text, items);
    }

    @Override
    public void addItem(DrawerItem item) {
        if (!(item instanceof SortDrawerItem))
            throw new IllegalArgumentException("SortDrawerGroupItem can contain only SortDrawerItems");

        super.addItem(item);
    }

    @Override
    public void childItemSelected(DrawerItem drawerItem) {
        if (!getItems().contains(drawerItem))
            throw new IllegalArgumentException("Group does not contain this item");

        SortDrawerItem sortItem = (SortDrawerItem) drawerItem;
        SortOrder nextSortOrder = SortOrder.values()[(sortItem.getSorting().ordinal() + 1) % SortOrder.values().length];
        sortItem.setSorting(nextSortOrder);

        List<DrawerItem> items = getItems();
        for (DrawerItem item : items) {
            if (item != sortItem) ((SortDrawerItem) item).setSorting(SortOrder.UNSORTED);
        }
    }

    public Comparator<Torrent> getComparator() {
        for (DrawerItem item : getItems()) {
            SortDrawerItem sortItem = (SortDrawerItem) item;
            if (sortItem.getSorting() != SortOrder.UNSORTED)
                return sortItem.getComparator();
        }
        return null;
    }
}
