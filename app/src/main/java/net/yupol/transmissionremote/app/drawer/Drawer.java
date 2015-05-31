package net.yupol.transmissionremote.app.drawer;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.sorting.TorrentComparators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

public class Drawer implements ListView.OnItemClickListener {

    private ListView drawerList;
    private List<DrawerGroupItem> groups;
    private DrawerListAdapter listAdapter;
    private OnItemSelectedListener itemSelectedListener;

    private TransmissionRemote.OnServerListChangedListener serversListener = new TransmissionRemote.OnServerListChangedListener() {
        @Override
        public void serverAdded(Server server) {
            addServers(server);
        }

        @Override
        public void serverRemoved(Server server) {
            removeServer(server);
        }

        @Override
        public void serverUpdated(Server server) {
            refresh();
        }
    };

    public Drawer(ListView drawerList) {
        this.drawerList = drawerList;
        initItemList(drawerList.getContext());
        listAdapter = new DrawerListAdapter(groups);
        drawerList.setAdapter(listAdapter);
        drawerList.setOnItemClickListener(this);

        TransmissionRemote app = (TransmissionRemote) drawerList.getContext().getApplicationContext();
        List<Server> servers = app.getServers();
        addServers(servers.toArray(new Server[servers.size()]));
        Server activeServer = app.getActiveServer();
        if (activeServer != null) setActiveServer(activeServer);
        app.addOnServerListChangedListener(serversListener);
    }

    private void initItemList(Context c) {
        groups = new ArrayList<>();
        // Servers
        groups.add(new ServerDrawerGroupItem(Groups.SERVERS.id(), R.string.drawer_servers, c,
                new EditServersDrawerItem(c)));

        // Sort by
        groups.add(new SortDrawerGroupItem(Groups.SORT_BY.id(), R.string.drawer_sort_by, c,
                new SortDrawerItem(R.string.drawer_sort_by_name, c, TorrentComparators.NAME),
                new SortDrawerItem(R.string.drawer_sort_by_size, c, TorrentComparators.SIZE),
                new SortDrawerItem(R.string.drawer_sort_by_time_remaining, c, TorrentComparators.TIME_REMAINING)));

        // Preferences
        groups.add(new DrawerGroupItem(Groups.PREFERENCES.id(), R.string.drawer_preferences, c,
                new ServerPrefsDrawerItem(R.string.drawer_preferences_server, c),
                new RemotePrefsDrawerItem(R.string.drawer_preferences_remote, c)));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (itemSelectedListener != null) {
            DrawerItem item = listAdapter.getItem(position);
            itemSelectedListener.onDrawerItemSelected(listAdapter.getGroupItem(item), item);
        }

        refresh();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.itemSelectedListener = listener;
    }

    public void setActiveServer(@Nonnull final Server server) {
        ServerDrawerGroupItem group = (ServerDrawerGroupItem) findGroupById(Groups.SERVERS.id());
        DrawerItem serverItem = FluentIterable.from(group.getItems()).firstMatch(new Predicate<DrawerItem>() {
            @Override public boolean apply(DrawerItem item) {
                return item instanceof ServerDrawerItem && server.equals(((ServerDrawerItem) item).getServer());
            }
        }).orNull();
        if (serverItem != null) {
            group.activateServerItem(serverItem);
        }
    }

    public void dispose() {
        TransmissionRemote app = (TransmissionRemote) drawerList.getContext().getApplicationContext();
        app.removeOnServerListChangedListener(serversListener);
    }

    private DrawerGroupItem findGroupById(final int id) {
        return FluentIterable.from(groups).firstMatch(new Predicate<DrawerGroupItem>() {
            @Override
            public boolean apply(DrawerGroupItem group) {
                return group.getId() == id;
            }
        }).orNull();
    }

    private void addServers(@Nonnull Server... servers) {
        DrawerGroupItem group = findGroupById(Groups.SERVERS.id());
        for (Server server : servers) {
            group.addItem(new ServerDrawerItem(server, drawerList.getContext()), group.getItems().size() - 1);
        }
        refresh();
    }

    private void removeServer(@Nonnull Server server) {
        DrawerGroupItem group = findGroupById(Groups.SERVERS.id());
        Iterator<DrawerItem> it = group.getItems().iterator();

        while (it.hasNext()) {
            DrawerItem item = it.next();
            if (item instanceof ServerDrawerItem) {
                ServerDrawerItem serverItem = (ServerDrawerItem) item;
                if (server.equals(serverItem.getServer())) {
                    it.remove();
                }
            }
        }
        refresh();
    }

    public void refresh() {
        listAdapter.notifyDataSetChanged();
    }

    public interface OnItemSelectedListener {
        void onDrawerItemSelected(DrawerGroupItem groupItem, DrawerItem item);
    }

    public enum Groups {
        SERVERS,
        SORT_BY,
        PREFERENCES;

        public int id() {
            return ordinal();
        }
    }
}
