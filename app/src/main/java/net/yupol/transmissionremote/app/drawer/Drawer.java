package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.server.Server;

import java.util.ArrayList;
import java.util.List;

public class Drawer implements ListView.OnItemClickListener {

    private List<DrawerGroupItem> groups;
    private DrawerListAdapter listAdapter;
    private OnItemSelectedListener listener;

    public Drawer(ListView drawerList) {
        initItemList(drawerList.getContext());
        listAdapter = new DrawerListAdapter(groups);
        drawerList.setAdapter(listAdapter);
        drawerList.setOnItemClickListener(this);
    }

    private void initItemList(Context c) {
        groups = new ArrayList<>();
        // Servers
        groups.add(new DrawerGroupItem(Groups.SERVERS.ordinal(), c.getString(R.string.drawer_servers)));

        // Actions
        groups.add(new DrawerGroupItem(Groups.ACTIONS.ordinal(), c.getString(R.string.drawer_actions),
                new DrawerItem(R.string.drawer_actions_open_torrent, c),
                new DrawerItem(R.string.drawer_actions_start_all_torrents, c),
                new DrawerItem(R.string.drawer_actions_pause_all_torrents, c)));

        // Filters
        groups.add(new DrawerGroupItem(Groups.FILTERS.ordinal(), c.getString(R.string.drawer_filters),
                new DrawerItem(R.string.drawer_filters_all, c),
                new DrawerItem(R.string.drawer_filters_active, c),
                new DrawerItem(R.string.drawer_filters_downloading, c),
                new DrawerItem(R.string.drawer_filters_seeding, c),
                new DrawerItem(R.string.drawer_filters_paused, c),
                new DrawerItem(R.string.drawer_filters_stopped, c)));

        // Sort by
        groups.add(new DrawerGroupItem(Groups.SORT_BY.ordinal(), c.getString(R.string.drawer_sort_by),
                new DrawerItem(R.string.drawer_sort_by_name, c),
                new DrawerItem(R.string.drawer_sort_by_size, c),
                new DrawerItem(R.string.drawer_sort_by_time_remaining, c)));

        // Preferences
        groups.add(new DrawerGroupItem(Groups.PREFERENCES.ordinal(), c.getString(R.string.drawer_preferences),
                new DrawerItem(R.string.drawer_preferences_server, c),
                new DrawerItem(R.string.drawer_preferences_remote, c)));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            DrawerItem item = listAdapter.getItem(position);
            listener.onDrawerItemSelected(listAdapter.getGroupItem(item), item);
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public void addServer(Server server) {
        findGroupById(Groups.SERVERS.ordinal()).addItem(new ServerDrawerItem(server));
        listAdapter.notifyDataSetChanged();
    }

    private DrawerGroupItem findGroupById(final int id) {
        return FluentIterable.from(groups).firstMatch(new Predicate<DrawerGroupItem>() {
            @Override
            public boolean apply(DrawerGroupItem group) {
                return group.getId() == id;
            }
        }).orNull();
    }

    public interface OnItemSelectedListener {
        public void onDrawerItemSelected(DrawerGroupItem groupItem, DrawerItem item);
    }

    private static enum Groups {
        SERVERS,
        ACTIONS,
        FILTERS,
        SORT_BY,
        PREFERENCES
    }
}
