package net.yupol.transmissionremote.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yupol.transmissionremote.app.actionbar.ActionBarNavigationAdapter;
import net.yupol.transmissionremote.app.actionbar.SpeedTextView;
import net.yupol.transmissionremote.app.actionbar.TurtleModeButton;
import net.yupol.transmissionremote.app.databinding.MainActivityBinding;
import net.yupol.transmissionremote.app.drawer.FreeSpaceFooterDrawerItem;
import net.yupol.transmissionremote.app.drawer.HeaderView;
import net.yupol.transmissionremote.app.drawer.SortDrawerItem;
import net.yupol.transmissionremote.app.filtering.Filter;
import net.yupol.transmissionremote.app.model.json.AddTorrentResult;
import net.yupol.transmissionremote.app.model.json.ServerSettings;
import net.yupol.transmissionremote.app.model.json.Torrent;
import net.yupol.transmissionremote.app.notifications.FinishedTorrentsNotificationManager;
import net.yupol.transmissionremote.app.opentorrent.DownloadLocationDialogFragment;
import net.yupol.transmissionremote.app.opentorrent.OpenAddressDialogFragment;
import net.yupol.transmissionremote.app.opentorrent.OpenByDialogFragment;
import net.yupol.transmissionremote.app.preferences.PreferencesActivity;
import net.yupol.transmissionremote.app.preferences.ServerPreferencesActivity;
import net.yupol.transmissionremote.app.preferences.ServersActivity;
import net.yupol.transmissionremote.app.server.AddServerActivity;
import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.sorting.SortOrder;
import net.yupol.transmissionremote.app.sorting.SortedBy;
import net.yupol.transmissionremote.app.torrentdetails.TorrentDetailsActivity;
import net.yupol.transmissionremote.app.torrentlist.EmptyServerFragment;
import net.yupol.transmissionremote.app.torrentlist.RemoveTorrentsDialogFragment;
import net.yupol.transmissionremote.app.torrentlist.TorrentListFragment;
import net.yupol.transmissionremote.app.transport.BaseSpiceActivity;
import net.yupol.transmissionremote.app.transport.NetworkError;
import net.yupol.transmissionremote.app.transport.TorrentUpdater;
import net.yupol.transmissionremote.app.transport.TransportManager;
import net.yupol.transmissionremote.app.transport.request.AddTorrentByFileRequest;
import net.yupol.transmissionremote.app.transport.request.AddTorrentByUrlRequest;
import net.yupol.transmissionremote.app.transport.request.ResponseFailureException;
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest;
import net.yupol.transmissionremote.app.transport.request.SessionSetRequest;
import net.yupol.transmissionremote.app.transport.request.StartTorrentRequest;
import net.yupol.transmissionremote.app.transport.request.StopTorrentRequest;
import net.yupol.transmissionremote.app.transport.request.TorrentRemoveRequest;
import net.yupol.transmissionremote.app.utils.DialogUtils;
import net.yupol.transmissionremote.app.utils.IconUtils;
import net.yupol.transmissionremote.app.utils.ThemeUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

@RuntimePermissions
public class MainActivity extends BaseSpiceActivity implements TorrentUpdater.TorrentUpdateListener,
        SharedPreferences.OnSharedPreferenceChangeListener, TransmissionRemote.OnSpeedLimitChangedListener,
        TorrentListFragment.OnTorrentSelectedListener, TorrentListFragment.ContextualActionBarListener,
        OpenByDialogFragment.OnOpenTorrentSelectedListener, OpenAddressDialogFragment.OnOpenMagnetListener,
        DownloadLocationDialogFragment.OnDownloadLocationSelectedListener,
        RemoveTorrentsDialogFragment.OnRemoveTorrentSelectionListener, NetworkErrorFragment.OnRefreshPressedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MIN_PREFS_UPDATE_INTERVAL = 5; // seconds

    public static int REQUEST_CODE_SERVER_PARAMS = 1;
    public static int REQUEST_CODE_CHOOSE_TORRENT = 2;

    private static final String TAG_EMPTY_SERVER = "tag_empty_server";
    private static final String TAG_PROGRESSBAR = "tag_progressbar";
    private static final String TAG_TORRENT_LIST = "tag_torrent_list";
    private static final String TAG_NETWORK_ERROR = "tag_network_error";
    private static final String TAG_OPEN_TORRENT_DIALOG = "tag_open_torrent_dialog";
    private static final String TAG_OPEN_TORRENT_BY_ADDRESS_DIALOG = "tag_open_torrent_by_address_dialog";
    private static final String TAG_DOWNLOAD_LOCATION_DIALOG = "tag_download_location_dialog";
    private static final String TAG_STORAGE_PERMISSION_NEVER_ASK_AGAIN_DIALOG = "tag_storage_permission_never_ask_again_dialog";

    private static final String MIME_TYPE_TORRENT = "application/x-bittorrent";
    private static final String SCHEME_MAGNET = "magnet";
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    private static final long UPDATE_REQUEST_DELAY = 500;

    private static final String KEY_DRAWER_SERVER_LIST_EXPANDED = "key_drawer_server_list_expanded";
    private static final String KEY_SEARCH_ACTION_EXPANDED = "key_search_action_expanded";
    private static final String KEY_SEARCH_QUERY = "key_search_query";
    private static final String KEY_HAS_TORRENT_LIST = "has_torrent_list";
    private static final String KEY_OPEN_TORRENT_URI = "key_open_torrent_uri";
    private static final String KEY_OPEN_TORRENT_SCHEME = "key_open_torrent_scheme";
    private static final String KEY_OPEN_TORRENT_PERMISSION_RATIONALE_OPEN = "key_open_torrent_permission_rationale_open";
    private static final String KEY_FAB_EXPANDED = "key_fab_expanded";

    private static final int DRAWER_ITEM_ID_SETTINGS = 101;

    private TransmissionRemote application;
    private TorrentUpdater torrentUpdater;

    private Timer prefsUpdateTimer;

    private MenuItem turtleModeItem;
    private TurtleModeButton turtleModeButton;
    private SpeedTextView downloadSpeedView;
    private SpeedTextView uploadSpeedView;

    private ActionBarNavigationAdapter toolbarSpinnerAdapter;
    private Toolbar bottomToolbar;
    private Drawer drawer;
    private HeaderView headerView;
    private RequestListener<AddTorrentResult> addTorrentResultListener = new RequestListener<AddTorrentResult>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            String message;
            if (spiceException.getCause() instanceof ResponseFailureException) {
                message = ((ResponseFailureException) spiceException.getCause()).getFailureMessage();
            } else {
                message = getString(R.string.error_failed_to_open_torrent);
            }
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(AddTorrentResult addTorrentResult) {
            if (addTorrentResult.torrentDuplicate != null) {
                Toast.makeText(MainActivity.this, R.string.error_duplicate_torrent, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, R.string.torrent_added_successfully, Toast.LENGTH_LONG).show();
            }
        }
    };

    private MenuItem bottomBarDownSpeedMenuItem;
    private MenuItem bottomBarUpSpeedMenuItem;

    private SearchView searchView;
    private MenuItem searchMenuItem;
    private boolean restoredSearchMenuItemExpanded = false;
    private CharSequence restoredSearchQuery = "";
    private String currentSearchQuery;

    private boolean hasTorrentList = false;

    private boolean isActivityResumed = false;
    private Uri openTorrentUri;
    private String openTorrentScheme;
    private boolean openTorrentUriOnResume = false;
    private boolean openTorrentUriWithSchemeOnResume = false;
    private boolean openTorrentPermissionRationaleOpen = false;
    private Spinner toolbarSpinner;
    private MainActivityBinding binding;
    private boolean showFab;
    private FreeSpaceFooterDrawerItem freeSpaceFooterDrawerItem;
    private FinishedTorrentsNotificationManager finishedTorrentsNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);

        application = TransmissionRemote.getApplication(this);
        finishedTorrentsNotificationManager = new FinishedTorrentsNotificationManager(this);

        setupActionBar();
        setupBottomToolbar();
        setupDrawer();
        setupFloatingActionButton();

        application.addOnSpeedLimitEnabledChangedListener(this);

        if (savedInstanceState != null) {
            openTorrentUri = savedInstanceState.getParcelable(KEY_OPEN_TORRENT_URI);
            openTorrentScheme = savedInstanceState.getString(KEY_OPEN_TORRENT_SCHEME);
            openTorrentPermissionRationaleOpen = savedInstanceState.getBoolean(KEY_OPEN_TORRENT_PERMISSION_RATIONALE_OPEN);
        }
    }

    private void setupActionBar() {
        setSupportActionBar(binding.toolbar);

        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, binding.toolbar, false);
        Toolbar.LayoutParams lp = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        binding.toolbar.addView(spinnerContainer, lp);
        toolbarSpinner = spinnerContainer.findViewById(R.id.toolbar_spinner);
        toolbarSpinnerAdapter = new ActionBarNavigationAdapter(this);
        toolbarSpinner.setAdapter(toolbarSpinnerAdapter);
        toolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id == ActionBarNavigationAdapter.ID_SERVER) {
                    Server server = (Server) toolbarSpinnerAdapter.getItem(position);
                    if (!server.equals(application.getActiveServer())) {
                        switchServer(server);
                    }
                } else if (id == ActionBarNavigationAdapter.ID_FILTER) {
                    Filter filter = (Filter) toolbarSpinnerAdapter.getItem(position);
                    if (!filter.equals(application.getActiveFilter())) {
                        application.setActiveFilter(filter);
                    }
                }
                toolbarSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupBottomToolbar() {
        bottomToolbar = findViewById(R.id.bottom_toolbar);
        if (bottomToolbar == null) return;

        turtleModeButton = bottomToolbar.findViewById(R.id.turtle_mode_button);
        turtleModeButton.setEnableChangedListener(new TurtleModeButton.OnEnableChangedListener() {
            @Override
            public void onEnableChanged(boolean isEnabled) {
                if (isEnabled == application.isSpeedLimitEnabled()) return;

                application.setSpeedLimitEnabled(!application.isSpeedLimitEnabled());
                updateSpeedLimitServerPrefs();
            }
        });

        bottomToolbar.inflateMenu(R.menu.bottom_toolbar_menu);
        Menu menu = bottomToolbar.getMenu();
        bottomBarDownSpeedMenuItem = menu.findItem(R.id.action_download_speed);
        bottomBarUpSpeedMenuItem = menu.findItem(R.id.action_upload_speed);
        downloadSpeedView = (SpeedTextView) bottomBarDownSpeedMenuItem.getActionView();
        uploadSpeedView = (SpeedTextView) bottomBarUpSpeedMenuItem.getActionView();
    }

    private void setupDrawer() {
        PrimaryDrawerItem settingsItem = new PrimaryDrawerItem().withName(R.string.action_settings)
                .withIdentifier(DRAWER_ITEM_ID_SETTINGS)
                .withIcon(GoogleMaterial.Icon.gmd_settings)
                .withSelectable(false);

        SwitchDrawerItem nightModeItem = new SwitchDrawerItem().withName(R.string.night_mode)
                .withIcon(CommunityMaterial.Icon.cmd_theme_light_dark)
                .withSelectable(false)
                .withChecked(ThemeUtils.isInNightMode(this))
                .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                        switchTheme(isChecked);
                    }
                });

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
                new SortDrawerItem(SortedBy.DATE_ADDED).withName(R.string.drawer_sort_by_date_added),
                new SortDrawerItem(SortedBy.SIZE).withName(R.string.drawer_sort_by_size),
                new SortDrawerItem(SortedBy.TIME_REMAINING).withName(R.string.drawer_sort_by_time_remaining),
                new SortDrawerItem(SortedBy.PROGRESS).withName(R.string.drawer_sort_by_progress),
                new SortDrawerItem(SortedBy.QUEUE_POSITION).withName(R.string.drawer_sort_by_queue_position),
                new SortDrawerItem(SortedBy.UPLOAD_RATIO).withName(R.string.drawer_sort_by_upload_ratio)
        };

        freeSpaceFooterDrawerItem = new FreeSpaceFooterDrawerItem();
        freeSpaceFooterDrawerItem.withSelectable(false);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(binding.toolbar)
                .withHeader(headerView)
                .addDrawerItems(new SectionDrawerItem().withName(R.string.drawer_sort_by).withDivider(false))
                .addDrawerItems((IDrawerItem[]) sortItems)
                .addStickyDrawerItems(
                        nightModeItem,
                        settingsItem,
                        freeSpaceFooterDrawerItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SortDrawerItem) {
                            handleSortItemClick((SortDrawerItem) drawerItem);
                            return true;
                        } else if (drawerItem.getIdentifier() == DRAWER_ITEM_ID_SETTINGS) {
                            startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
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

    private void setupFloatingActionButton() {

        binding.addTorrentByFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.addTorrentButton.collapse();
                onOpenTorrentByFile();
            }
        });

        binding.addTorrentByMagnetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.addTorrentButton.collapse();
                onOpenTorrentByAddress();
            }
        });

        binding.addTorrentByFileButton.setIconDrawable(
                new IconicsDrawable(this, CommunityMaterial.Icon.cmd_file_outline)
                        .paddingRes(R.dimen.fab_icon_padding)
                        .colorRes(R.color.text_primary_inverse));

        binding.addTorrentByMagnetButton.setIconDrawable(
                new IconicsDrawable(this, CommunityMaterial.Icon.cmd_magnet)
                        .paddingRes(R.dimen.fab_icon_padding)
                        .colorRes(R.color.text_primary_inverse));

        binding.addTorrentButton.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                binding.fabOverlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                binding.fabOverlay.setVisibility(View.GONE);
            }
        });

        binding.fabOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.addTorrentButton.collapse();
            }
        });

        binding.fabOverlay.setVisibility(binding.addTorrentButton.isExpanded() ? View.VISIBLE : View.GONE);
    }

    private void switchTheme(boolean nightMode) {
        ThemeUtils.setIsInNightMode(this, nightMode);
        recreate();
    }

    @Override
    protected void onDestroy() {
        application.removeOnSpeedLimitEnabledChangedListener(this);
        turtleModeButton = null;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (openTorrentPermissionRationaleOpen) {
            openTorrentPermissionRationaleOpen = false;
            MainActivityPermissionsDispatcher.openTorrentFileByUriWithSchemeWithCheck(this, openTorrentUri, openTorrentScheme);
        } else {
            openTorrentFromIntent();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        openTorrentFromIntent();
    }

    private void openTorrentFromIntent() {
        Uri data = getIntent().getData();
        if (data == null) return;

        getIntent().setData(null);

        if (application.getActiveServer() == null) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.error_msg_open_torrent_no_server)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }

        String scheme = data.getScheme();
        if (SCHEME_MAGNET.equals(scheme)) {
            openTorrentByMagnet(data.toString());
        } else if (ContentResolver.SCHEME_FILE.equals(scheme) || ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            openTorrentUri = data;
            openTorrentScheme = scheme;
            MainActivityPermissionsDispatcher.openTorrentFileByUriWithSchemeWithCheck(this, data, scheme);
        } else if (SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme)) {
            openTorrentByRemoteFile(data);
        }
    }

    @SuppressLint("InlinedApi")
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void openTorrentFileByUriWithScheme(Uri uri, String scheme) {
        if (!isActivityResumed) {
            openTorrentUriWithSchemeOnResume = true;
            return;
        }

        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            try {
                openTorrentByLocalFile(getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                Toast.makeText(this, getString(R.string.error_cannot_read_file_msg), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Can't open stream for '" + uri + "'", e);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try {
                openTorrentByLocalFile(getContentResolver().openInputStream(uri));
            } catch (IOException ex) {
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int dataColumn = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    String path = cursor.getString(dataColumn);
                    cursor.close();

                    File file = new File(path);
                    try {
                        openTorrentByLocalFile(FileUtils.openInputStream(file));
                    } catch (IOException e) {
                        Toast.makeText(this, getString(R.string.error_cannot_read_file_msg), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Can't open stream for '" + path + "'", e);
                    }
                }
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.error_failed_to_open_downloaded_torrent, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Can't open torrent file", e);
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressLint("InlinedApi")
    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showStoragePermissionRationale(final PermissionRequest request) {
        openTorrentPermissionRationaleOpen = true;
        new AlertDialog.Builder(this)
                .setMessage(R.string.storage_permission_rationale)
                .setPositiveButton(R.string.storage_permission_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        openTorrentPermissionRationaleOpen = false;
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.storage_permission_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openTorrentPermissionRationaleOpen = false;
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @SuppressLint("InlinedApi")
    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void onStoragePermissionNeverAskAgain() {
        DialogUtils.showDialogAllowingStateLoss(
                new StoragePermissionNeverAskAgainDialog(),
                getSupportFragmentManager(), TAG_STORAGE_PERMISSION_NEVER_ASK_AGAIN_DIALOG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityResumed = true;
        getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        List<Server> servers = application.getServers();
        if (servers.isEmpty()) {
            showEmptyServerFragment();
            drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            binding.toolbar.setVisibility(View.GONE);
            if (bottomToolbar != null) bottomToolbar.setVisibility(View.GONE);
        } else {
            switchServer(application.getActiveServer());
            drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            binding.toolbar.setVisibility(View.VISIBLE);
            if (bottomToolbar != null) bottomToolbar.setVisibility(View.VISIBLE);
        }

        binding.addTorrentButton.collapseImmediately();

        showFab = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.show_add_torrent_fab_key), true);
        boolean isListVisible = getTorrentListFragment() != null;
        binding.addTorrentButton.setVisibility(showFab && isListVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (openTorrentUriWithSchemeOnResume) {
            openTorrentUriWithSchemeOnResume = false;
            openTorrentFileByUriWithScheme(openTorrentUri, openTorrentScheme);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityResumed = false;

        if (torrentUpdater != null) {
            torrentUpdater.stop();
        }

        stopPreferencesUpdateTimer();

        getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        application.persist();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_DRAWER_SERVER_LIST_EXPANDED, drawer.switchedDrawerContent());
        if (searchMenuItem != null) {
            outState.putBoolean(KEY_SEARCH_ACTION_EXPANDED, searchMenuItem.isActionViewExpanded());
            outState.putCharSequence(KEY_SEARCH_QUERY, searchView.getQuery());
        } else {
            outState.putBoolean(KEY_SEARCH_ACTION_EXPANDED, restoredSearchMenuItemExpanded);
            outState.putCharSequence(KEY_SEARCH_QUERY, restoredSearchQuery);
        }
        outState.putBoolean(KEY_HAS_TORRENT_LIST, hasTorrentList);

        outState.putParcelable(KEY_OPEN_TORRENT_URI, openTorrentUri);
        outState.putString(KEY_OPEN_TORRENT_SCHEME, openTorrentScheme);
        outState.putBoolean(KEY_OPEN_TORRENT_PERMISSION_RATIONALE_OPEN, openTorrentPermissionRationaleOpen);
        outState.putBoolean(KEY_FAB_EXPANDED, binding.addTorrentButton.isExpanded());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getBoolean(KEY_DRAWER_SERVER_LIST_EXPANDED, false)) {
            headerView.showServersList();
        }

        restoredSearchMenuItemExpanded = savedInstanceState.getBoolean(KEY_SEARCH_ACTION_EXPANDED, false);
        restoredSearchQuery = savedInstanceState.getCharSequence(KEY_SEARCH_QUERY, "");

        hasTorrentList = savedInstanceState.getBoolean(KEY_HAS_TORRENT_LIST, false);

        boolean isFabExpanded = savedInstanceState.getBoolean(KEY_FAB_EXPANDED, false);
        if (isFabExpanded) {
            binding.addTorrentButton.expand();
        } else {
            binding.addTorrentButton.collapseImmediately();
        }
        binding.fabOverlay.setVisibility(isFabExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.torrent_list_menu, menu);

        IconUtils.setMenuIcon(this, menu, R.id.action_start_all_torrents, FontAwesome.Icon.faw_play);
        IconUtils.setMenuIcon(this, menu, R.id.action_pause_all_torrents, FontAwesome.Icon.faw_pause);

        turtleModeItem = menu.findItem(R.id.action_turtle_mode);
        updateTurtleModeActionIcon();

        final MenuItem downloadItem = menu.findItem(R.id.action_download_speed);
        if (downloadItem != null) {
            downloadSpeedView = (SpeedTextView) downloadItem.getActionView();
        }
        final MenuItem uploadItem = menu.findItem(R.id.action_upload_speed);
        if (uploadItem != null) {
            uploadSpeedView = (SpeedTextView) uploadItem.getActionView();
        }

        searchMenuItem = menu.findItem(R.id.action_search);
        if (searchMenuItem == null) {
            searchMenuItem = bottomToolbar.getMenu().findItem(R.id.action_search);
        }
        IconUtils.setMenuIcon(this, searchMenuItem, FontAwesome.Icon.faw_search);

        searchView = (SearchView) searchMenuItem.getActionView();
        // iconifiedByDefault must be false to avoid closing SearchView by close button (close button only clears text)
        searchView.setIconifiedByDefault(false);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        // Workaround issue #47. Setting query hint in code to avoid crash in SearchView#updateQueryHint
        searchView.setQueryHint(getString(R.string.search_hing));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (bottomBarDownSpeedMenuItem != null) bottomBarDownSpeedMenuItem.setVisible(false);
                if (bottomBarUpSpeedMenuItem != null) bottomBarUpSpeedMenuItem.setVisible(false);

                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchView.setQuery("", false);
                currentSearchQuery = null;
                TorrentListFragment torrentListFragment = getTorrentListFragment();
                if (torrentListFragment != null) {
                    torrentListFragment.closeSearch();
                }

                if (bottomBarDownSpeedMenuItem != null) bottomBarDownSpeedMenuItem.setVisible(true);
                if (bottomBarUpSpeedMenuItem != null) bottomBarUpSpeedMenuItem.setVisible(true);

                searchView.clearFocus();

                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handleSearch(newText);
                return false;
            }
        });

        if (restoredSearchMenuItemExpanded) {
            searchMenuItem.expandActionView();
            searchView.setQuery(restoredSearchQuery, true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void handleSearch(String query) {
        if (!query.equals(currentSearchQuery)) {
            currentSearchQuery = query;
            TorrentListFragment torrentListFragment = getTorrentListFragment();
            if (torrentListFragment != null) {
                torrentListFragment.search(query);
            }
        }
    }

    @Nullable
    private TorrentListFragment getTorrentListFragment() {
        return (TorrentListFragment) getSupportFragmentManager().findFragmentByTag(TAG_TORRENT_LIST);
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
                new OpenByDialogFragment().show(getFragmentManager(), TAG_OPEN_TORRENT_DIALOG);
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
        if (requestCode == REQUEST_CODE_SERVER_PARAMS) {
            if (resultCode == RESULT_OK) {
                Server server = data.getParcelableExtra(AddServerActivity.EXTRA_SEVER);
                addNewServer(server);
                switchServer(server);
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE_TORRENT) {
            if (resultCode == RESULT_OK) {
                openTorrentUri = data.getData();
                openTorrentUriOnResume = true;
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (!openTorrentUriOnResume) return;

        openTorrentUriOnResume = false;

        try {
            openTorrentByLocalFile(getContentResolver().openInputStream(openTorrentUri));
        } catch (IOException ex) {
            Cursor cursor = getContentResolver().query(openTorrentUri, null, null, null, null);
            if (cursor == null) {
                String msg = getString(R.string.error_file_does_not_exists_msg, openTorrentUri.toString());
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                return;
            }
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            String fileName = cursor.getString(nameIndex);
            cursor.close();
            String extension = FilenameUtils.getExtension(fileName);
            if (!"torrent".equals(extension)) {
                String msg = getResources().getString(R.string.error_wrong_file_extension_msg, extension);
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                return;
            }
            try {
                openTorrentByLocalFile(getContentResolver().openInputStream(Uri.parse(fileName)));
            } catch (FileNotFoundException e) {
                String msg = getResources().getString(R.string.error_file_does_not_exists_msg, fileName);
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) drawer.closeDrawer();
        else if (searchMenuItem.isActionViewExpanded()) searchMenuItem.collapseActionView();
        else if (binding.addTorrentButton.isExpanded()) binding.addTorrentButton.collapse();
        else super.onBackPressed();
    }

    @Override
    public void speedLimitEnabledChanged(boolean isEnabled) {
        updateTurtleModeActionIcon();
    }

    @Override
    public void onTorrentUpdate(List<Torrent> torrents) {
        application.setTorrents(torrents);
        hasTorrentList = true;

        if (application.isNotificationEnabled()) {
            finishedTorrentsNotificationManager.checkForFinishedTorrents(application.getActiveServer(), torrents);
        }

        if (getTorrentListFragment() == null) {
            showTorrentListFragment();
        }

        updateSpeedActions(torrents);

        toolbarSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNetworkError(NetworkError error) {
        int messageRes;

        switch (error) {
            case NO_NETWORK:
                messageRes = R.string.network_error_message_no_network;
                break;
            case UNAUTHORIZED:
                messageRes = R.string.network_error_message_unauthorized;
                break;
            default:
                messageRes = R.string.network_error_message_connection_error;
        }

        hasTorrentList = false;
        showNetworkErrorFragment(getString(messageRes));
    }

    @Override
    public void onRefreshPressed() {
        showProgressbarFragment();
        torrentUpdater.scheduleUpdate(0);
    }

    @Override
    public void onTorrentSelected(Torrent torrent) {
        Intent intent = new Intent(this, TorrentDetailsActivity.class);
        intent.putExtra(TorrentDetailsActivity.EXTRA_TORRENT, torrent);
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

    @Override
    public void onCABOpen() {
        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onCABClose() {
        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onOpenTorrentByFile() {
        showFileChooser();
    }

    @Override
    public void onOpenTorrentByAddress() {
        new OpenAddressDialogFragment().show(getFragmentManager(), MainActivity.TAG_OPEN_TORRENT_BY_ADDRESS_DIALOG);
    }

    @Override
    public void onOpenMagnet(String uri) {
        openTorrentByMagnet(uri);
    }

    @Override
    public void onDownloadLocationSelected(Bundle args, final String downloadDir, final boolean startWhenAdded) {
        switch (args.getInt(DownloadLocationDialogFragment.KEY_REQUEST_CODE)) {
            case DownloadLocationDialogFragment.REQUEST_CODE_BY_LOCAL_FILE:
                getTransportManager().doRequest(
                        new AddTorrentByFileRequest(args.getByteArray(DownloadLocationDialogFragment.KEY_FILE_BYTES), downloadDir, !startWhenAdded),
                        addTorrentResultListener);
                break;
            case DownloadLocationDialogFragment.REQUEST_CODE_BY_REMOTE_FILE:
                Uri fileUri = args.getParcelable(DownloadLocationDialogFragment.KEY_FILE_URI);
                new RetrieveTorrentContentAsyncTask() {
                    @Override
                    protected void onPostExecute(byte[] bytes) {
                        if (bytes != null) {
                            TransportManager tm = getTransportManager();
                            if (tm.isStarted()) {
                                tm.doRequest(new AddTorrentByFileRequest(bytes, downloadDir, !startWhenAdded), addTorrentResultListener);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.error_cannot_read_file_msg), Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(fileUri);
                break;
            case DownloadLocationDialogFragment.REQUEST_CODE_BY_MAGNET:
                String magnetUri = args.getString(DownloadLocationDialogFragment.KEY_MAGNET_URI);
                if (magnetUri != null) {
                    getTransportManager().doRequest(
                            new AddTorrentByUrlRequest(magnetUri, downloadDir, !startWhenAdded),
                            addTorrentResultListener);
                } else {
                    throw new IllegalStateException("Magnet Uri is null");
                }
                break;
        }
    }

    @Override
    public void onServerSelected(Server server) {
        switchServer(server);
    }

    @Override
    public void onRemoveTorrentsSelected(int[] torrentsToRemove, boolean removeData) {
        getTransportManager().doRequest(new TorrentRemoveRequest(torrentsToRemove, removeData), null);
        torrentUpdater.scheduleUpdate(UPDATE_REQUEST_DELAY);
    }

    public void openAddServerActivity(View view) {
        Intent intent = new Intent(this, AddServerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SERVER_PARAMS);
    }

    private void addNewServer(Server server) {
        application.addServer(server);
    }

    private void switchServer(Server server) {
        if (!server.equals(application.getActiveServer())) {
            hasTorrentList = false;
        }
        application.setActiveServer(server);
        toolbarSpinner.setSelection(toolbarSpinnerAdapter.getServerPosition(server));

        // Stop old server connections
        if (torrentUpdater != null) {
            torrentUpdater.stop();
        }
        stopPreferencesUpdateTimer();
        if (hasTorrentList) {
            showTorrentListFragment();
        } else {
            showProgressbarFragment();
        }

        // Start new server connections
        List<Server> servers = application.getServers();
        headerView.setServers(servers, servers.indexOf(server));

        torrentUpdater = new TorrentUpdater(getTransportManager(), MainActivity.this, application.getUpdateInterval());
        torrentUpdater.start();

        startPreferencesUpdateTimer();

        toolbarSpinnerAdapter.notifyDataSetChanged();
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
                        if (drawer != null && !drawer.switchedDrawerContent()) {
                            drawer.updateStickyFooterItem(freeSpaceFooterDrawerItem.withFreeSpace(serverSettings.getDownloadDirFreeSpace()));
                        }
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

        binding.addTorrentButton.setVisibility(View.GONE);
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

        binding.addTorrentButton.setVisibility(View.GONE);
    }

    private void showTorrentListFragment() {
        FragmentManager fm = getSupportFragmentManager();
        TorrentListFragment torrentListFragment = (TorrentListFragment) fm.findFragmentByTag(TAG_TORRENT_LIST);
        if (torrentListFragment == null) {
            torrentListFragment = new TorrentListFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.torrent_list_container, torrentListFragment, TAG_TORRENT_LIST);
            ft.commit();
        }

        binding.addTorrentButton.setVisibility(showFab ? View.VISIBLE : View.GONE);
    }

    private void showNetworkErrorFragment(String message) {
        FragmentManager fm = getSupportFragmentManager();
        NetworkErrorFragment fragment = (NetworkErrorFragment) fm.findFragmentByTag(TAG_NETWORK_ERROR);
        if (fragment == null) {
            fragment = new NetworkErrorFragment();
            Bundle args = new Bundle();
            args.putString(NetworkErrorFragment.KEY_MESSAGE, message);
            fragment.setArguments(args);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.torrent_list_container, fragment, TAG_NETWORK_ERROR);
            ft.commitAllowingStateLoss();
        } else {
            fragment.setErrorMessage(message);
        }

        binding.addTorrentButton.setVisibility(View.GONE);
    }

    private void updateTurtleModeActionIcon() {
        if (turtleModeItem != null) {
            turtleModeItem.setIcon(application.isSpeedLimitEnabled() ? R.drawable.ic_turtle_active : R.drawable.ic_turtle_default);
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
        if (downloadSpeedView != null) downloadSpeedView.setSpeed(totalDownloadRate);
        if (uploadSpeedView != null) uploadSpeedView.setSpeed(totalUploadRate);
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

    private void openTorrentByLocalFile(final InputStream fileStream) {
        DialogFragment dialog = new DownloadLocationDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DownloadLocationDialogFragment.KEY_REQUEST_CODE, DownloadLocationDialogFragment.REQUEST_CODE_BY_LOCAL_FILE);
        try {
            args.putByteArray(DownloadLocationDialogFragment.KEY_FILE_BYTES, IOUtils.toByteArray(fileStream));
        } catch (IOException e) {
            Log.e(TAG, "Failed to read file stream", e);
            Toast.makeText(MainActivity.this, getString(R.string.error_cannot_read_file_msg), Toast.LENGTH_SHORT).show();
            return;
        } finally {
            try {
                fileStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close input stream", e);
            }
        }
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), TAG_DOWNLOAD_LOCATION_DIALOG);
    }

    private void openTorrentByRemoteFile(final Uri fileUri) {
        DialogFragment dialog = new DownloadLocationDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DownloadLocationDialogFragment.KEY_REQUEST_CODE, DownloadLocationDialogFragment.REQUEST_CODE_BY_REMOTE_FILE);
        args.putParcelable(DownloadLocationDialogFragment.KEY_FILE_URI, fileUri);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), TAG_DOWNLOAD_LOCATION_DIALOG);
    }

    private void openTorrentByMagnet(final String magnetUri) {
        DialogFragment dialog = new DownloadLocationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DownloadLocationDialogFragment.KEY_REQUEST_CODE, DownloadLocationDialogFragment.REQUEST_CODE_BY_MAGNET);
        bundle.putString(DownloadLocationDialogFragment.KEY_MAGNET_URI, magnetUri);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), TAG_DOWNLOAD_LOCATION_DIALOG);
    }

    private void startAllTorrents() {
        getTransportManager().doRequest(new StartTorrentRequest(application.getTorrents()), null);
        torrentUpdater.scheduleUpdate(UPDATE_REQUEST_DELAY);
    }

    private void pauseAllTorrents() {
        getTransportManager().doRequest(new StopTorrentRequest(application.getTorrents()), null);
        torrentUpdater.scheduleUpdate(UPDATE_REQUEST_DELAY);
    }

    private static abstract class RetrieveTorrentContentAsyncTask extends AsyncTask<Uri, Void, byte[]> {

        @Override
        protected byte[] doInBackground(Uri... torrentFileUris) {
            String uri = torrentFileUris[0].toString();
            InputStream inputStream = null;
            try {
                inputStream = new URL(uri).openConnection().getInputStream();
                return IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                Log.e(TAG, "Failed to retrieve Uri '" + uri + "'", e);
            } finally {
                if (inputStream != null) try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close InputStream", e);
                }
            }
            return null;
        }

        @Override
        protected abstract void onPostExecute(byte[] bytes);
    }

    public static class StoragePermissionNeverAskAgainDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getContext())
                    .setMessage(R.string.storage_permission_never_ask_again_message)
                    .setPositiveButton(R.string.app_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }
    }
}
