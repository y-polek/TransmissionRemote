package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.sorting.TorrentComparators;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.utils.Filters;

import java.util.ArrayList;
import java.util.List;

public class Drawer implements ListView.OnItemClickListener {

    private List<DrawerGroupItem> groups;
    private DrawerListAdapter listAdapter;
    private OnItemSelectedListener listener;

    public Drawer(ListView drawerList, TransportManager tm) {
        initItemList(drawerList.getContext(), tm);
        listAdapter = new DrawerListAdapter(groups);
        drawerList.setAdapter(listAdapter);
        drawerList.setOnItemClickListener(this);
    }

    private void initItemList(Context c, TransportManager tm) {
        groups = new ArrayList<>();
        // Servers
        groups.add(new DrawerGroupItem(Groups.SERVERS.id(), c.getString(R.string.drawer_servers),
                new NewServerDrawerItem(c)));

        // Actions
        groups.add(new DrawerGroupItem(Groups.ACTIONS.id(), c.getString(R.string.drawer_actions),
                new DrawerItem(R.string.drawer_actions_open_torrent, c),
                new StartAllTorrentsDrawerItem(c, tm),
                new PauseAllTorrentsDrawerItem(c, tm)));

        // Filters
        groups.add(new DrawerGroupItem(Groups.FILTERS.id(), c.getString(R.string.drawer_filters),
                new FilterDrawerItem(R.string.drawer_filters_all, c, Filters.ALL),
                new FilterDrawerItem(R.string.drawer_filters_active, c, Filters.ACTIVE),
                new FilterDrawerItem(R.string.drawer_filters_downloading, c, Filters.DOWNLOADING),
                new FilterDrawerItem(R.string.drawer_filters_seeding, c, Filters.SEEDING),
                new FilterDrawerItem(R.string.drawer_filters_paused, c, Filters.PAUSED)));

        // Sort by
        groups.add(new SortDrawerGroupItem(Groups.SORT_BY.id(), c.getString(R.string.drawer_sort_by),
                new SortDrawerItem(R.string.drawer_sort_by_name, c, TorrentComparators.NAME),
                new SortDrawerItem(R.string.drawer_sort_by_size, c, TorrentComparators.SIZE),
                new SortDrawerItem(R.string.drawer_sort_by_time_remaining, c, TorrentComparators.TIME_REMAINING)));

        // Preferences
        groups.add(new DrawerGroupItem(Groups.PREFERENCES.id(), c.getString(R.string.drawer_preferences),
                new ServerPrefsDrawerItem(R.string.drawer_preferences_server, c),
                new RemotePrefsDrawerItem(R.string.drawer_preferences_remote, c)));
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

    public void refresh() {
        listAdapter.notifyDataSetChanged();
    }

    public void addServer(Server server) {
        DrawerGroupItem group = findGroupById(Groups.SERVERS.id());
        group.addItem(new ServerDrawerItem(server), group.getItems().size() - 1);
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

    public static enum Groups {
        SERVERS,
        ACTIONS,
        FILTERS,
        SORT_BY,
        PREFERENCES;

        public int id() {
            return ordinal();
        }
    }
}
