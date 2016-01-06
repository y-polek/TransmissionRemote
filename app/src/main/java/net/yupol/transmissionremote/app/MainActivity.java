package net.yupol.transmissionremote.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.actionbar.ActionBarNavigationAdapter;
import net.yupol.transmissionremote.app.actionbar.SpeedTextView;
import net.yupol.transmissionremote.app.actionbar.TurtleModeButton;
import net.yupol.transmissionremote.app.drawer.Drawer;
import net.yupol.transmissionremote.app.drawer.DrawerGroupItem;
import net.yupol.transmissionremote.app.drawer.DrawerItem;
import net.yupol.transmissionremote.app.drawer.ServerDrawerItem;
import net.yupol.transmissionremote.app.drawer.ServerPrefsDrawerItem;
import net.yupol.transmissionremote.app.drawer.SortDrawerGroupItem;
import net.yupol.transmissionremote.app.filtering.Filter;
import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.opentorrent.DownloadLocationDialogFragment;
import net.yupol.transmissionremote.app.opentorrent.OpenAddressDialogFragment;
import net.yupol.transmissionremote.app.opentorrent.OpenByDialogFragment;
import net.yupol.transmissionremote.app.preferences.ServerPreferencesActivity;
import net.yupol.transmissionremote.app.server.AddServerActivity;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.torrentdetails.TorrentDetailsActivity;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.TorrentUpdater;
import net.yupol.transmissionremote.app.transport.request.AddTorrentByFileRequest;
import net.yupol.transmissionremote.app.transport.request.AddTorrentByUrlRequest;
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest;
import net.yupol.transmissionremote.app.transport.request.SessionSetRequest;
import net.yupol.transmissionremote.app.transport.request.StartTorrentRequest;
import net.yupol.transmissionremote.app.transport.request.StopTorrentRequest;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseSpiceActivity implements Drawer.OnItemSelectedListener,
            TorrentUpdater.TorrentUpdateListener, SharedPreferences.OnSharedPreferenceChangeListener,
            TransmissionRemote.OnSpeedLimitChangedListener, TorrentListFragment.OnTorrentSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MIN_PREFS_UPDATE_INTERVAL = 5; // seconds

    public static int REQUEST_CODE_SERVER_PARAMS = 1;
    public static int REQUEST_CODE_CHOOSE_TORRENT = 2;

    private static String TAG_EMPTY_SERVER = "tag_empty_server";
    private static String TAG_PROGRESSBAR = "tag_progressbar";
    private static String TAG_TORRENT_LIST = "tag_torrent_list";
    private static String TAG_OPEN_TORRENT_DIALOG = "tag_open_torrent_dialog";
    private static String TAG_OPEN_TORRENT_BY_ADDRESS_DIALOG = "tag_open_torrent_by_address_dialog";
    private static String TAG_DOWNLOAD_LOCATION_DIALOG = "tag_download_location_dialog";

    private static final String MIME_TYPE_TORRENT = "application/x-bittorrent";

    private TransmissionRemote application;
    private TorrentUpdater torrentUpdater;

    private Drawer drawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private Timer prefsUpdateTimer;

    private MenuItem turtleModeItem;
    private TurtleModeButton turtleModeButton;
    private SpeedTextView downloadSpeedView;
    private SpeedTextView uploadSpeedView;

    private ActionBarNavigationAdapter navigationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        application = TransmissionRemote.getApplication(this);

        setupActionBar();
        setupBottomToolbar();
        setupDrawer();

        showProgressbarFragment();

        application.addOnSpeedLimitEnabledChangedListener(this);
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar_toolbar);
        setSupportActionBar(toolbar);

        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);
        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.toolbar_spinner);
        navigationAdapter = new ActionBarNavigationAdapter(this);
        spinner.setAdapter(navigationAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id == ActionBarNavigationAdapter.ID_SERVER) {
                    Server server = (Server) navigationAdapter.getItem(position);
                    if (!server.equals(application.getActiveServer())) {
                        switchServer(server);
                    }
                } else if (id == ActionBarNavigationAdapter.ID_FILTER) {
                    Filter filter = (Filter) navigationAdapter.getItem(position);
                    if (!filter.equals(application.getActiveFilter())) {
                        application.setActiveFilter(filter);
                    }
                }
                navigationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupBottomToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
        if (toolbar == null) return;

        turtleModeButton = (TurtleModeButton) toolbar.findViewById(R.id.turtle_mode_button);
        turtleModeButton.setEnableChangedListener(new TurtleModeButton.OnEnableChangedListener() {
            @Override
            public void onEnableChanged(boolean isEnabled) {
                if (isEnabled == application.isSpeedLimitEnabled()) return;

                application.setSpeedLimitEnabled(!application.isSpeedLimitEnabled());
                updateSpeedLimitServerPrefs();
            }
        });


        toolbar.inflateMenu(R.menu.speed_status_menu);
        Menu menu = toolbar.getMenu();
        downloadSpeedView = (SpeedTextView) MenuItemCompat.getActionView(menu.findItem(R.id.action_download_speed));
        uploadSpeedView = (SpeedTextView) MenuItemCompat.getActionView(menu.findItem(R.id.action_upload_speed));
    }

    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ListView drawerList = (ListView) findViewById(R.id.drawer_list);

        drawer = new Drawer(drawerList);
        drawer.setOnItemSelectedListener(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        application.removeOnSpeedLimitEnabledChangedListener(this);
        drawer.dispose();
        turtleModeButton = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        List<Server> servers = application.getServers();
        if (servers.isEmpty()) {
            showEmptyServerFragment();
        } else {
            switchServer(application.getActiveServer());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (torrentUpdater != null) {
            torrentUpdater.stop();
        }

        stopPreferencesUpdateTimer();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        application.persistServers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.torrent_list_menu, menu);

        turtleModeItem = menu.findItem(R.id.action_turtle_mode);
        updateTurtleModeActionIcon();

        MenuItem downloadItem = menu.findItem(R.id.action_download_speed);
        if (downloadItem != null) {
            downloadSpeedView = (SpeedTextView) MenuItemCompat.getActionView(downloadItem);
        }
        MenuItem uploadItem = menu.findItem(R.id.action_upload_speed);
        if (uploadItem != null) {
            uploadSpeedView = (SpeedTextView) MenuItemCompat.getActionView(uploadItem);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case R.id.action_turtle_mode:
                application.setSpeedLimitEnabled(!application.isSpeedLimitEnabled());
                updateTurtleModeActionIcon();
                updateSpeedLimitServerPrefs();
                return true;
            case R.id.action_open_torrent:
                openTorrent();
                return true;
            case R.id.action_start_all_torrents:
                startAllTorrents();
                return true;
            case R.id.action_pause_all_torrents:
                pauseAllTorrents();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == REQUEST_CODE_SERVER_PARAMS) {
            if (resultCode == RESULT_OK) {
                Server server = data.getParcelableExtra(AddServerActivity.EXTRA_SEVER);
                addNewServer(server);
                switchServer(server);
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE_TORRENT) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Log.d(TAG, "File URI: " + uri);
                Log.d(TAG, "Path: " + uri.getPath());

                openTorrent(new File(uri.getPath()));
            }
        }
    }

    @Override
    public void onDrawerItemSelected(DrawerGroupItem group, DrawerItem item) {
        Log.d(TAG, "item '" + item.getText() + "' in group '" + group.getText() + "' selected");

        item.itemSelected();
        group.childItemSelected(item);
        drawerLayout.closeDrawers();

        if (group.getId() == Drawer.Groups.SERVERS.id()) {
            if (item instanceof ServerDrawerItem) {
                Server server = ((ServerDrawerItem) item).getServer();
                if (!server.equals(application.getActiveServer())) {
                    switchServer(server);
                }
            }
        } else if (group.getId() == Drawer.Groups.SORT_BY.id()) {
            TorrentListFragment torrentListFragment =
                    (TorrentListFragment) getSupportFragmentManager().findFragmentByTag(TAG_TORRENT_LIST);
            if (torrentListFragment != null)
                torrentListFragment.setSort(((SortDrawerGroupItem) group).getComparator());
        } else if (group.getId() == Drawer.Groups.PREFERENCES.id()) {
            if (item instanceof ServerPrefsDrawerItem) {
                startActivity(new Intent(this, ServerPreferencesActivity.class));
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
    public void speedLimitEnabledChanged(boolean isEnabled) {
        updateTurtleModeActionIcon();
    }

    @Override
    public void onTorrentUpdate(List<Torrent> torrents) {
        application.setTorrents(torrents);

        showTorrentListFragment();

        updateSpeedActions(torrents);

        navigationAdapter.notifyDataSetChanged();

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
    public void onTorrentSelected(Torrent torrent) {
        Intent intent = new Intent(this, TorrentDetailsActivity.class);
        intent.putExtra(TorrentDetailsActivity.EXTRA_NAME_TORRENT, torrent);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.update_interval_key))) {
            if (torrentUpdater != null) {
                torrentUpdater.setTimeout(application.getUpdateInterval());
            }
        }
    }

    public void addServerButtonClicked(View view) {
        Intent intent = new Intent(this, AddServerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SERVER_PARAMS);
    }

    private void addNewServer(Server server) {
        application.addServer(server);
    }

    private void switchServer(Server server) {
        application.setActiveServer(server);
        drawer.setActiveServer(server);

        // Stop old server connections
        if (torrentUpdater != null) {
            torrentUpdater.stop();
        }
        stopPreferencesUpdateTimer();
        showProgressbarFragment();

        // Start new server connections
        torrentUpdater = new TorrentUpdater(getTransportManager(), MainActivity.this, application.getUpdateInterval());
        torrentUpdater.start();

        startPreferencesUpdateTimer();
    }

    private void startPreferencesUpdateTimer() {
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
                        application.setSpeedLimitEnabled(serverSettings.isAltSpeedLimitEnabled());
                        application.setDefaultDownloadDir(serverSettings.getDownloadDir());
                    }
                });
            }
        }, 0, TimeUnit.SECONDS.toMillis(Math.max(application.getUpdateInterval(), MIN_PREFS_UPDATE_INTERVAL)));
    }

    private void stopPreferencesUpdateTimer() {
        if (prefsUpdateTimer != null) {
            prefsUpdateTimer.cancel();
        }
    }

    private void showEmptyServerFragment() {
        FragmentManager fm = getSupportFragmentManager();
        EmptyServerFragment emptyServerFragment = (EmptyServerFragment) fm.findFragmentByTag(TAG_EMPTY_SERVER);
        if (emptyServerFragment == null) {
            emptyServerFragment = new EmptyServerFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.torrent_list_container, emptyServerFragment, TAG_EMPTY_SERVER);
            ft.commit();
        }
    }

    private void showProgressbarFragment() {
        FragmentManager fm = getSupportFragmentManager();
        ProgressbarFragment progressbarFragment = (ProgressbarFragment) fm.findFragmentByTag(TAG_PROGRESSBAR);
        if (progressbarFragment == null) {
            progressbarFragment = new ProgressbarFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.torrent_list_container, progressbarFragment, TAG_PROGRESSBAR);
            ft.commit();
        }
    }

    private void showTorrentListFragment() {
        FragmentManager fm = getSupportFragmentManager();
        TorrentListFragment torrentListFragment = (TorrentListFragment) fm.findFragmentByTag(TAG_TORRENT_LIST);
        if (torrentListFragment == null) {
            torrentListFragment = new TorrentListFragment();
            torrentListFragment.setOnTorrentSelectedListener(this);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.torrent_list_container, torrentListFragment, TAG_TORRENT_LIST);
            ft.commit();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(MIME_TYPE_TORRENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, getResources().getString(R.string.select_torrent_file)),
                    MainActivity.REQUEST_CODE_CHOOSE_TORRENT);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this,
                    getResources().getString(R.string.error_install_file_manager_msg),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void openTorrent(final File file) {
        if (!file.exists()) {
            String name = file.getName();
            String msg = getResources().getString(R.string.error_file_does_not_exists_msg, name.isEmpty() ? "" : "'" + name + "'");
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

        String extension = FilenameUtils.getExtension(file.getName());
        if (!extension.equals("torrent")) {
            String msg = getResources().getString(R.string.error_wrong_file_extension_msg, extension.isEmpty() ? "" : "'." + extension + "'");
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            return;
        }

        new DownloadLocationDialogFragment().show(getFragmentManager(), TAG_DOWNLOAD_LOCATION_DIALOG, new DownloadLocationDialogFragment.OnResultListener() {
            @Override
            public void onAddPressed(String downloadDir, boolean startWhenAdded) {
                try {
                    getTransportManager().doRequest(new AddTorrentByFileRequest(file, downloadDir, !startWhenAdded), null);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_cannot_read_file_msg), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTurtleModeActionIcon() {
        if (turtleModeItem != null) {
            turtleModeItem.setIcon(application.isSpeedLimitEnabled() ? R.drawable.turtle_blue : R.drawable.turtle_white);
        }
        if (turtleModeButton != null) {
            turtleModeButton.setEnabled(application.isSpeedLimitEnabled());
        }
    }

    private void updateSpeedLimitServerPrefs() {

        JSONObject sessionArgs = new JSONObject();
        try {
            sessionArgs.put(ServerSettings.ALT_SPEED_LIMIT_ENABLED, application.isSpeedLimitEnabled());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create session arguments JSON object", e);
        }

        getTransportManager().doRequest(new SessionSetRequest(sessionArgs), new RequestListener<Void>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d(TAG, "Failed to update session settings");
            }

            @Override
            public void onRequestSuccess(Void aVoid) {
                Log.e(TAG, "Session settings updated successfully");
            }
        });
    }

    private void updateSpeedActions(Collection<Torrent> torrents) {
        int totalDownloadRate = 0;
        int totalUploadRate = 0;
        for (Torrent torrent : torrents) {
            totalDownloadRate += torrent.getDownloadRate();
            totalUploadRate += torrent.getUploadRate();
        }
        downloadSpeedView.setSpeed(totalDownloadRate);
        uploadSpeedView.setSpeed(totalUploadRate);
    }

    private void openTorrent() {
        new OpenByDialogFragment().show(getFragmentManager(), TAG_OPEN_TORRENT_DIALOG, new OpenByDialogFragment.OnSelectionListener() {
            @Override
            public void byFile() {
                showFileChooser();
            }

            @Override
            public void byAddress() {
                new OpenAddressDialogFragment().show(getFragmentManager(), MainActivity.TAG_OPEN_TORRENT_BY_ADDRESS_DIALOG, new OpenAddressDialogFragment.OnResultListener() {
                    @Override
                    public void onOpenPressed(final String uri) {
                        new DownloadLocationDialogFragment().show(getFragmentManager(), TAG_DOWNLOAD_LOCATION_DIALOG, new DownloadLocationDialogFragment.OnResultListener() {
                            @Override
                            public void onAddPressed(String downloadDir, boolean startWhenAdded) {
                                getTransportManager().doRequest(new AddTorrentByUrlRequest(uri, downloadDir, !startWhenAdded), null);
                            }
                        });
                    }
                });
            }
        });
    }

    private void startAllTorrents() {
        getTransportManager().doRequest(new StartTorrentRequest(application.getTorrents()), null);
    }

    private void pauseAllTorrents() {
        getTransportManager().doRequest(new StopTorrentRequest(application.getTorrents()), null);
    }
}
