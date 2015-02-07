package net.yupol.transmissionremote.app;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.drawer.Drawer;
import net.yupol.transmissionremote.app.drawer.DrawerGroupItem;
import net.yupol.transmissionremote.app.drawer.DrawerItem;
import net.yupol.transmissionremote.app.drawer.NewServerDrawerItem;
import net.yupol.transmissionremote.app.drawer.ServerDrawerItem;
import net.yupol.transmissionremote.app.drawer.ServerPrefsDrawerItem;
import net.yupol.transmissionremote.app.drawer.SortDrawerGroupItem;
import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.preferences.ServerPreferencesActivity;
import net.yupol.transmissionremote.app.server.AddServerActivity;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.PortChecker;
import net.yupol.transmissionremote.app.transport.TorrentUpdater;
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest;
import net.yupol.transmissionremote.app.transport.request.SessionSetRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseSpiceActivity implements Drawer.OnItemSelectedListener,
            TorrentUpdater.TorrentUpdateListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MIN_PREFS_UPDATE_INTERVAL = 5; // seconds

    public static int REQUEST_CODE_SERVER_PARAMS = 1;
    public static int REQUEST_CODE_SERVER_PREFERENCES = 2;

    private static String TAG_PROGRESSBAR = "tag_progressbar";
    private static String TAG_TORRENT_LIST = "tag_torrent_list";

    private TransmissionRemote application;
    private PortChecker portChecker;
    private TorrentUpdater torrentUpdater;

    private Drawer drawer;
    private ActionBarDrawerToggle drawerToggle;
    private TorrentListFragment torrentListFragment;
    private ToolbarFragment toolbarFragment;

    private Timer prefsUpdateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        application = TransmissionRemote.getApplication(this);

        ListView drawerList = (ListView) findViewById(R.id.drawer_list);

        drawer = new Drawer(drawerList, getTransportManager());
        drawer.setOnItemSelectedListener(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
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
        drawerLayout.setDrawerListener(drawerToggle);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        showProgressbarFragment();

        FragmentManager fm = getFragmentManager();
        toolbarFragment = (ToolbarFragment) fm.findFragmentById(R.id.toolbar_container);
        if (toolbarFragment == null) {
            toolbarFragment = new ToolbarFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.toolbar_container, toolbarFragment);
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        List<Server> servers = application.getServers();
        if (servers.isEmpty()) {
            Intent intent = new Intent(this, AddServerActivity.class);
            intent.putExtra(AddServerActivity.PARAM_CANCELABLE, false);
            startActivityForResult(intent, REQUEST_CODE_SERVER_PARAMS);
        } else {
            startPortChecker();
        }

        prefsUpdateTimer = new Timer("Preferences update timer");
        prefsUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getTransportManager().doRequest(new SessionGetRequest(), new RequestListener<ServerSettings>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.e(TAG, "Failed to obtain server settings");
                    }

                    @Override
                    public void onRequestSuccess(ServerSettings serverSettings) {
                        application.setSpeedLimitEnabled(serverSettings.isAltSpeedEnabled());
                    }
                });
            }
        }, 0, TimeUnit.SECONDS.toMillis(Math.max(application.getUpdateInterval(), MIN_PREFS_UPDATE_INTERVAL)));

    }

    @Override
    protected void onPause() {
        super.onPause();

        stopPortChecker();

        if (torrentUpdater != null) {
            torrentUpdater.stop();
        }
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

        prefsUpdateTimer.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == REQUEST_CODE_SERVER_PARAMS) {
            if (resultCode == RESULT_OK) {
                Server server = data.getParcelableExtra(AddServerActivity.EXTRA_SEVER);
                addNewServer(server);
            }
        } else if (requestCode == REQUEST_CODE_SERVER_PREFERENCES) {
            if (resultCode == RESULT_OK) {
                String prefsExtra = data.getStringExtra(ServerPreferencesActivity.EXTRA_SERVER_PREFERENCES);
                JSONObject preferences;
                try {
                    preferences = new JSONObject(prefsExtra);
                } catch (JSONException e) {
                    Log.e(TAG, "Can't parse session preferences as JSON object: '" + prefsExtra + "'");
                    return;
                }

                Log.d(TAG, "preferences: " + preferences);
                getTransportManager().doRequest(new SessionSetRequest(preferences), new RequestListener<Void>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.e(TAG, "Failed to set server preferences");
                    }

                    @Override
                    public void onRequestSuccess(Void aVoid) {
                        Log.i(TAG, "Server preferences set successfully");
                    }
                });
            }
        }
    }

    @Override
    public void onDrawerItemSelected(DrawerGroupItem group, DrawerItem item) {
        Log.d(TAG, "item '" + item.getText() + "' in group '" + group.getText() + "' selected");

        item.itemSelected();
        group.childItemSelected(item);

        if (group.getId() == Drawer.Groups.SERVERS.id()) {
            if (item instanceof NewServerDrawerItem) {
                startActivityForResult(new Intent(this, AddServerActivity.class), REQUEST_CODE_SERVER_PARAMS);
            } else if (item instanceof ServerDrawerItem) {
                Server server = ((ServerDrawerItem) item).getServer();
                switchServer(server);
            }
        } else if (group.getId() == Drawer.Groups.SORT_BY.id()) {
            if (torrentListFragment != null)
                torrentListFragment.setSort(((SortDrawerGroupItem) group).getComparator());
        } else if (group.getId() == Drawer.Groups.PREFERENCES.id()) {
            if (item instanceof ServerPrefsDrawerItem) {
                startActivityForResult(
                        new Intent(this, ServerPreferencesActivity.class),
                        REQUEST_CODE_SERVER_PREFERENCES);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTorrentUpdate(List<Torrent> torrents) {
        application.setTorrents(torrents);

        showTorrentListFragment();

        toolbarFragment.torrentsUpdated(torrents);

        String text = Joiner.on("\n").join(FluentIterable.from(torrents).transform(new Function<Torrent, String>() {
            @Override
            public String apply(Torrent torrent) {
                String percents = String.format("%.2f", torrent.getPercentDone() * 100);
                return torrent.getStatus() + " " + percents + "% " + torrent.getName();
            }
        }));

        Log.d(TAG, "Torrents:\n" + text);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.update_interval_key))) {
            if (torrentUpdater != null) {
                torrentUpdater.setTimeout(application.getUpdateInterval());
            }
        }
    }

    private void addNewServer(Server server) {
        application.addServer(server);
        drawer.addServers(server);

        if (application.getServers().size() == 1) {
            application.setActiveServer(server);
            torrentUpdater = new TorrentUpdater(getTransportManager(), this, application.getUpdateInterval());
            // TODO: drawer.setActiveServer(server);
        }
    }

    private void switchServer(Server server) {
        Log.d(TAG, "server selected: " + server.getName() + " active server: " + application.getActiveServer().getName());
        if (server.equals(application.getActiveServer())) return;

        application.setActiveServer(server);

        if (torrentUpdater != null) {
            torrentUpdater.stop();
        }

        // stop old server's port checker if any and start for new server
        stopPortChecker();
        showProgressbarFragment();
        toolbarFragment.reset();
        startPortChecker();
    }

    private void startPortChecker() {
        portChecker = new PortChecker(getTransportManager(), new PortChecker.PortCheckResultListener() {
            @Override
            public void onPortCheckResults(boolean isOpen) {
                // FIXME: check if port is opened
                    /*if (result.isOpen()) {
                        torrentUpdater.start();
                    } else {
                        Toast.makeText(MainActivity.this, "Port " + application.getActiveServer().getPort() +
                                " is closed. Check Transmission settings.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Port " + application.getActiveServer().getPort() + " is closed");
                    }*/
                torrentUpdater = new TorrentUpdater(getTransportManager(), MainActivity.this, application.getUpdateInterval());
                torrentUpdater.start();
            }
        });
        portChecker.startCheck();
    }

    private void stopPortChecker() {
        if (portChecker != null && portChecker.isRunning()) {
            portChecker.cancel();
        }
    }

    private void showProgressbarFragment() {
        FragmentManager fm = getFragmentManager();
        ProgressbarFragment progressbarFragment = (ProgressbarFragment) fm.findFragmentByTag(TAG_PROGRESSBAR);
        if (progressbarFragment == null) {
            progressbarFragment = new ProgressbarFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.torrent_list_container, progressbarFragment, TAG_PROGRESSBAR);
            ft.commit();
        }
        torrentListFragment = null;
    }

    private void showTorrentListFragment() {
        FragmentManager fm = getFragmentManager();
        torrentListFragment = (TorrentListFragment) fm.findFragmentByTag(TAG_TORRENT_LIST);
        if (torrentListFragment == null) {
            torrentListFragment = new TorrentListFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.torrent_list_container, torrentListFragment, TAG_TORRENT_LIST);
            ft.commit();
        }
    }
}
