package net.yupol.transmissionremote.app.drawer;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.yupol.transmissionremote.app.R;

public class Drawer implements ListView.OnItemClickListener {

    private DrawerGroupItem[] groups = new DrawerGroupItem[] {
            // Servers
            new DrawerGroupItem(R.string.drawer_servers),

            // Server
            new DrawerGroupItem(R.string.drawer_actions,
                    new DrawerItem(R.string.drawer_actions_open_torrent),
                    new DrawerItem(R.string.drawer_actions_start_all_torrents),
                    new DrawerItem(R.string.drawer_actions_pause_all_torrents)),

            // Filters
            new DrawerGroupItem(R.string.drawer_filters,
                    new DrawerItem(R.string.drawer_filters_all),
                    new DrawerItem(R.string.drawer_filters_active),
                    new DrawerItem(R.string.drawer_filters_downloading),
                    new DrawerItem(R.string.drawer_filters_seeding),
                    new DrawerItem(R.string.drawer_filters_paused),
                    new DrawerItem(R.string.drawer_filters_stopped)),

            // Sort by
            new DrawerGroupItem(R.string.drawer_sort_by,
                    new DrawerItem(R.string.drawer_sort_by_name),
                    new DrawerItem(R.string.drawer_sort_by_size),
                    new DrawerItem(R.string.drawer_sort_by_time_remaining)),

            // Preferences
            new DrawerGroupItem(R.string.drawer_preferences,
                    new DrawerItem(R.string.drawer_preferences_server),
                    new DrawerItem(R.string.drawer_preferences_remote))

    };

    private DrawerListAdapter listAdapter;
    private OnItemSelectedListener listener;

    public Drawer(ListView drawerList) {
        listAdapter = new DrawerListAdapter(groups);
        drawerList.setAdapter(listAdapter);
        drawerList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            listener.onDrawerItemSelected(listAdapter.getItem(position));
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnItemSelectedListener {
        public void onDrawerItemSelected(DrawerItem item);
    }
}
