package net.yupol.transmissionremote.app.drawer;

public class DrawerGroupItem extends DrawerItem {

    private DrawerItem[] items;

    public DrawerGroupItem(int textResId, DrawerItem ... items) {
        super(textResId);
        this.items = items;
    }

    public DrawerItem[] getItems() {
        return items;
    }
}
