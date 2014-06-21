package net.yupol.transmissionremote.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import net.yupol.transmissionremote.app.drawer.Drawer;
import net.yupol.transmissionremote.app.drawer.DrawerItem;
import net.yupol.transmissionremote.app.server.AddServerActivity;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.Remote;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity implements Drawer.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static int REQUEST_CODE_SERVER_PARAMS = 1;

    private ActionBarDrawerToggle mDrawerToggle;

    private TorrentListFragment torrentListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ListView drawerList = (ListView) findViewById(R.id.drawer_list);

        Drawer drawer = new Drawer(drawerList);
        drawer.setOnItemSelectedListener(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                    R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(getTitle());
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle(getTitle());
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

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

        TransmissionRemote app = TransmissionRemote.getApplication(this);
        List<Server> servers = app.getServers();
        // TODO: add servers to drawer
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
                Log.d(TAG, "New server: " + server);
            }
        }
    }

    @Override
    public void onDrawerItemSelected(DrawerItem item) {
        Log.d(TAG, "item selected: " + item.getText(this));
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

    }
}
