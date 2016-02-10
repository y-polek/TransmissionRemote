package net.yupol.transmissionremote.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.actionbar.ActionBarNavigationAdapter;
import net.yupol.transmissionremote.app.actionbar.SpeedTextView;
import net.yupol.transmissionremote.app.actionbar.TurtleModeButton;
import net.yupol.transmissionremote.app.drawer.HeaderView;
import net.yupol.transmissionremote.app.drawer.SortDrawerItem;
import net.yupol.transmissionremote.app.filtering.Filter;
import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.opentorrent.DownloadLocationDialogFragment;
import net.yupol.transmissionremote.app.opentorrent.OpenAddressDialogFragment;
import net.yupol.transmissionremote.app.opentorrent.OpenByDialogFragment;
import net.yupol.transmissionremote.app.preferences.RemotePreferencesActivity;
import net.yupol.transmissionremote.app.preferences.ServerPreferencesActivity;
import net.yupol.transmissionremote.app.preferences.ServersActivity;
import net.yupol.transmissionremote.app.server.AddServerActivity;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.sorting.SortOrder;
import net.yupol.transmissionremote.app.sorting.SortedBy;
import net.yupol.transmissionremote.app.torrentdetails.TorrentDetailsActivity;
import net.yupol.transmissionremote.app.torrentlist.EmptyServerFragment;
import net.yupol.transmissionremote.app.torrentlist.TorrentListFragment;
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
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseSpiceActivity implements TorrentUpdater.TorrentUpdateListener,
        SharedPreferences.OnSharedPreferenceChangeListener, TransmissionRemote.OnSpeedLimitChangedListener,
        TorrentListFragment.OnTorrentSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MIN_PREFS_UPDATE_INTERVAL = 5; // seconds

    public static int REQUEST_CODE_SERVER_PARAMS = 1;
    public static int REQUEST_CODE_CHOOSE_TORRENT = 2;

    private static final String TAG_EMPTY_SERVER = "tag_empty_server";
    private static final String TAG_PROGRESSBAR = "tag_progressbar";
    private static final String TAG_TORRENT_LIST = "tag_torrent_list";
    private static final String TAG_OPEN_TORRENT_DIALOG = "tag_open_torrent_dialog";
    private static final String TAG_OPEN_TORRENT_BY_ADDRESS_DIALOG = "tag_open_torrent_by_address_dialog";
    private static final String TAG_DOWNLOAD_LOCATION_DIALOG = "tag_download_location_dialog";

    private static final String MIME_TYPE_TORRENT = "application/x-bittorrent";

    private static final long UPDATE_REQUEST_DELAY = 500;

    private static final int DRAWER_ITEM_ID_SETTINGS = 0;

    private static final String KEY_DRAWER_SERVER_LIST_EXPANDED = "KEY_DRAWER_SERVER_LIST_EXPANDED";

    private TransmissionRemote application;
    private TorrentUpdater torrentUpdater;

    private Timer prefsUpdateTimer;

    private MenuItem turtleModeItem;
    private TurtleModeButton turtleModeButton;
    private SpeedTextView downloadSpeedView;
    private SpeedTextView uploadSpeedView;

    private ActionBarNavigationAdapter navigationAdapter;
    private Toolbar toolbar;
    private Toolbar bottomToolbar;
    private Drawer drawer;
    private HeaderView headerView;

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
        toolbar = (Toolbar) findViewById(R.id.actionbar_toolbar);
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
        bottomToolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
        if (bottomToolbar == null) return;

        turtleModeButton = (TurtleModeButton) bottomToolbar.findViewById(R.id.turtle_mode_button);
        turtleModeButton.setEnableChangedListener(new TurtleModeButton.OnEnableChangedListener() {
            @Override
            public void onEnableChanged(boolean isEnabled) {
                if (isEnabled == application.isSpeedLimitEnabled()) return;

                application.setSpeedLimitEnabled(!application.isSpeedLimitEnabled());
                updateSpeedLimitServerPrefs();
            }
        });

        bottomToolbar.inflateMenu(R.menu.speed_status_menu);
        Menu menu = bottomToolbar.getMenu();
        downloadSpeedView = (SpeedTextView) MenuItemCompat.getActionView(menu.findItem(R.id.action_download_speed));
        uploadSpeedView = (SpeedTextView) MenuItemCompat.getActionView(menu.findItem(R.id.action_upload_speed));
    }

    private void setupDrawer() {
        PrimaryDrawerItem settingsItem = new PrimaryDrawerItem().withName(R.string.action_settings)
                .withIcon(GoogleMaterial.Icon.gmd_settings)
                .withSelectable(false)
                .withIdentifier(DRAWER_ITEM_ID_SETTINGS);

        headerView = new HeaderView(this);
        headerView.setHeaderListener(new HeaderView.HeaderListener() {
            @Override
            public void onSettingsPressed() {
                startActivity(new Intent(MainActivity.this, ServerPreferencesActivity.class));
            }

            @Override
            public void onServerSelected(Server server) {
                switchServer(server);
            }

            @Override
            public void onAddServerPressed() {
                openAddServerActivity(null);
            }

            @Override
            public void onManageServersPressed() {
                startActivity(new Intent(MainActivity.this, ServersActivity.class));
            }
        });

        final SortDrawerItem[] sortItems = new SortDrawerItem[] {
                new SortDrawerItem(SortedBy.NAME).withName(R.string.drawer_sort_by_name),
                new SortDrawerItem(SortedBy.SIZE).withName(R.string.drawer_sort_by_size),
                new SortDrawerItem(SortedBy.TIME_REMAINING).withName(R.string.drawer_sort_by_time_remaining)
        };

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(headerView)
                .addDrawerItems(new SectionDrawerItem().withName(R.string.drawer_sort_by).withDivider(false))
                .addDrawerItems(sortItems)
                .addStickyDrawerItems(
                        settingsItem
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getIdentifier()) {
                            case DRAWER_ITEM_ID_SETTINGS:
                                startActivity(new Intent(MainActivity.this, RemotePreferencesActivity.class));
                                return true;
                        }

                        if (drawerItem instanceof SortDrawerItem) {
                            handleSortItemClick((SortDrawerItem) drawerItem);
                            return true;
                        }

                        return false;
                    }

                    private void handleSortItemClick(SortDrawerItem selectedItem) {
                        SortOrder prevSortOrder = selectedItem.getSortOrder();
                        SortOrder sortOrder;
                        if (prevSortOrder == null) sortOrder = SortOrder.ASCENDING;
                        else sortOrder = prevSortOrder == SortOrder.ASCENDING ? SortOrder.DESCENDING : SortOrder.ASCENDING;
                        for (SortDrawerItem item : sortItems) {
                            if (item != selectedItem) {
                                item.setSortOrder(null);
                                item.withSetSelected(false);
                                drawer.updateItem(item);
                            }
                        }
                        selectedItem.setSortOrder(sortOrder);
                        application.setSorting(selectedItem.getSortedBy(), sortOrder);
                    }
                }).build();

        headerView.setDrawer(drawer);
        List<Server> servers = application.getServers();
        headerView.setServers(servers, servers.indexOf(application.getActiveServer()));

        SortedBy persistedSortedBy = application.getSortedBy();
        SortOrder persistedSortOrder = application.getSortOrder();
        for (SortDrawerItem item : sortItems) {
            if (item.getSortedBy() == persistedSortedBy) {
                item.setSortOrder(persistedSortOrder);
                item.withSetSelected(true);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        application.removeOnSpeedLimitEnabledChangedListener(this);
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
            drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toolbar.setVisibility(View.GONE);
            if (bottomToolbar != null) bottomToolbar.setVisibility(View.GONE);
        } else {
            switchServer(application.getActiveServer());
            drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toolbar.setVisibility(View.VISIBLE);
            if (bottomToolbar != null) bottomToolbar.setVisibility(View.VISIBLE);
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
        application.persist();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_DRAWER_SERVER_LIST_EXPANDED, drawer.switchedDrawerContent());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean(KEY_DRAWER_SERVER_LIST_EXPANDED, false)) {
            headerView.showServersList();
        }
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) drawer.closeDrawer();
        else super.onBackPressed();
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
                String percents = String.format(Locale.getDefault(), "%.2f", torrent.getPercentDone() * 100);
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

    public void openAddServerActivity(View view) {
        Intent intent = new Intent(this, AddServerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SERVER_PARAMS);
    }

    private void addNewServer(Server server) {
        application.addServer(server);
    }

    private void switchServer(Server server) {
        application.setActiveServer(server);

        // Stop old server connections
        if (torrentUpdater != null) {
            torrentUpdater.stop();
        }
        stopPreferencesUpdateTimer();
        showProgressbarFragment();

        // Start new server connections
        List<Server> servers = application.getServers();
        headerView.setServers(servers, servers.indexOf(server));

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
            ft.commitAllowingStateLoss();
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
        torrentUpdater.scheduleUpdate(UPDATE_REQUEST_DELAY);
    }

    private void pauseAllTorrents() {
        getTransportManager().doRequest(new StopTorrentRequest(application.getTorrents()), null);
        torrentUpdater.scheduleUpdate(UPDATE_REQUEST_DELAY);
    }
}
