package net.yupol.transmissionremote.app.drawer;

import net.yupol.transmissionremote.app.R;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DrawerGroupItem extends DrawerItem {

    private int id;
    private List<DrawerItem> items;

    public DrawerGroupItem(int id, String text, DrawerItem ... items) {
        super(text);
        this.id = id;
        this.items = new LinkedList<>(Arrays.asList(items));
    }

    public int getId() {
        return id;
    }

    public void addItem(DrawerItem item) {
        items.add(item);
    }

    public void addItem(DrawerItem item, int position) {
        if (position > items.size()) {
            throw new IllegalArgumentException("position argument > count of items in group. position: "
                    + position + ", items in group: " + items.size());
        }
        items.add(position, item);
    }

    public void childItemSelected(DrawerItem item) {}

    public List<DrawerItem> getItems() {
        return items;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.drawer_list_group_item;
    }
}
