package net.yupol.transmissionremote.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import net.yupol.transmissionremote.app.drawer.Drawer;
import net.yupol.transmissionremote.app.drawer.DrawerGroupItem;
import net.yupol.transmissionremote.app.drawer.DrawerItem;
import net.yupol.transmissionremote.app.drawer.NewServerDrawerItem;
import net.yupol.transmissionremote.app.drawer.ServerDrawerItem;
import net.yupol.transmissionremote.app.server.AddServerActivity;
import net.yupol.transmissionremote.app.server.Server;

import java.util.List;

public class MainActivity extends Activity implements Drawer.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static int REQUEST_CODE_SERVER_PARAMS = 1;

    private TransmissionRemote application;

    private Drawer drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private TorrentListFragment torrentListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        application = TransmissionRemote.getApplication(this);

        ListView drawerList = (ListView) findViewById(R.id.drawer_list);

        drawer = new Drawer(drawerList);
        for (Server server : application.getServers())
            drawer.addServer(server);
        drawer.setOnItemSelectedListener(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                    R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (getActionBar() != null) getActionBar().setTitle(getTitle());
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (getActionBar() != null) getActionBar().setTitle(getTitle());
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        FragmentManager fm = getFragmentManager();

        torrentListFragment = (TorrentListFragment) fm.findFragmentById(R.id.torrent_list_container);
        if (torrentListFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.torrent_list_container, new TorrentListFragment());
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Server> servers = application.getServers();
        if (servers.isEmpty()) {
            Intent intent = new Intent(this, AddServerActivity.class);
            intent.putExtra(AddServerActivity.PARAM_CANCELABLE, false);
            startActivityForResult(intent, REQUEST_CODE_SERVER_PARAMS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SERVER_PARAMS) {
            if (resultCode == RESULT_OK) {
                Server server = data.getParcelableExtra(AddServerActivity.EXTRA_SEVER);
                addNewServer(server);
            }
        }
    }

    @Override
    public void onDrawerItemSelected(DrawerGroupItem group, DrawerItem item) {
        Log.d(TAG, "item '" + item.getText() + "' in group '" + group.getText() + "' selected");

        if (group.getId() == Drawer.Groups.SERVERS.id()) {
            if (item instanceof NewServerDrawerItem) {
                startActivityForResult(new Intent(this, AddServerActivity.class), REQUEST_CODE_SERVER_PARAMS);
            } else if (item instanceof ServerDrawerItem) {
                Server server = ((ServerDrawerItem) item).getServer();
                Log.d(TAG, "selected server: " + server);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void addNewServer(Server server) {
        application.addServer(server);
        drawer.addServer(server);
    }
}
