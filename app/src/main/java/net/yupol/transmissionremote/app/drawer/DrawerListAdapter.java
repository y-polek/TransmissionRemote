package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;

import java.util.List;

public  class DrawerListAdapter extends BaseAdapter {

    private List<DrawerGroupItem> groups;

    public DrawerListAdapter(List<DrawerGroupItem> groups) {
        this.groups = groups;
    }

    @Override
    public int getCount() {
        int count = 0;
        for (DrawerGroupItem group : groups) {
            count++;
            count += group.getItems().size();
        }
        return count;
    }

    @Override
    public DrawerItem getItem(int position) {

        int i = 0;
        for (DrawerGroupItem group : groups) {
            if (i == position)
                return group;
            i++;
            for (DrawerItem item : group.getItems()) {
                if (i == position)
                    return item;
                i++;
            }
        }
        return null;
    }

    public DrawerGroupItem getGroupItem(DrawerItem drawerItem) {
        for (DrawerGroupItem group : groups) {
            if (drawerItem.equals(group))
                return group;
            for (DrawerItem item : group.getItems()) {
                if (drawerItem.equals(item))
                    return group;
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        DrawerItem item = getItem(position);
        if (item instanceof  DrawerGroupItem)
            return false;
        return super.isEnabled(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(parent);
    }
}
