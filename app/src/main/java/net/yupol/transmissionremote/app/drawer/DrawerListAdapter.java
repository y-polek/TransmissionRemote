package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.yupol.transmissionremote.app.R;

public  class DrawerListAdapter extends BaseAdapter {

    private DrawerGroupItem[] groups;

    public DrawerListAdapter(DrawerGroupItem[] groups) {
        this.groups = groups;
    }

    @Override
    public int getCount() {
        int count = 0;
        for (DrawerGroupItem group : groups) {
            count++;
            count += group.getItems().length;
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

        DrawerItem item = getItem(position);

        View itemView;

        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = li.inflate(R.layout.drawer_list_item, parent, false);
        } else {
            itemView = convertView;
        }

        TextView groupTextView = (TextView) itemView.findViewById(R.id.drawer_list_group_text);
        TextView itemTextView = (TextView) itemView.findViewById(R.id.drawer_list_item_text);
        View groupDivider = itemView.findViewById(R.id.drawer_list_group_divider);
        View itemDivider = itemView.findViewById(R.id.drawer_list_item_divider);

        TextView visibleView, invisibleView;
        View visibleDivider, invisibleDivider;
        if (item instanceof DrawerGroupItem) {
            visibleView = groupTextView;
            invisibleView = itemTextView;
            visibleDivider = groupDivider;
            invisibleDivider = itemDivider;
        } else {
            visibleView = itemTextView;
            invisibleView = groupTextView;
            visibleDivider = itemDivider;
            invisibleDivider = groupDivider;
        }

        visibleView.setVisibility(View.VISIBLE);
        invisibleView.setVisibility(View.GONE);
        visibleDivider.setVisibility(View.VISIBLE);
        invisibleDivider.setVisibility(View.GONE);

        visibleView.setText(item.getTextResource());

        return itemView;
    }
}
