package net.yupol.transmissionremote.app.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import net.yupol.transmissionremote.app.BaseMvpActivity;
import net.yupol.transmissionremote.app.R;
import net.yupol.transmissionremote.app.TransmissionRemote;
import net.yupol.transmissionremote.app.actionbar.ActionBarNavigationAdapter;
import net.yupol.transmissionremote.app.actionbar.SpeedTextView;
import net.yupol.transmissionremote.app.actionbar.TurtleModeButton;
import net.yupol.transmissionremote.app.databinding.MainActivityBinding;
import net.yupol.transmissionremote.app.drawer.FreeSpaceFooterDrawerItem;
import net.yupol.transmissionremote.app.drawer.HeaderView;
import net.yupol.transmissionremote.app.drawer.SortDrawerItem;
import net.yupol.transmissionremote.app.filtering.Filter;
import net.yupol.transmissionremote.app.model.TorrentViewModel;
import net.yupol.transmissionremote.app.preferences.Preferences;
import net.yupol.transmissionremote.app.preferences.PreferencesActivity;
import net.yupol.transmissionremote.app.preferences.ServerListActivity;
import net.yupol.transmissionremote.app.preferences.ServerPreferencesActivity;
import net.yupol.transmissionremote.app.server.AddServerActivity;
import net.yupol.transmissionremote.app.server.ServerDetailsActivity;
import net.yupol.transmissionremote.app.sorting.SortOrder;
import net.yupol.transmissionremote.app.sorting.SortedBy;
import net.yupol.transmissionremote.app.torrentdetails.TorrentDetailsActivity;
import net.yupol.transmissionremote.app.torrentlist.TorrentListFragment;
import net.yupol.transmissionremote.app.utils.DividerItemDecoration;
import net.yupol.transmissionremote.app.utils.IconUtils;
import net.yupol.transmissionremote.app.utils.ThemeUtils;
import net.yupol.transmissionremote.device.clipboard.Clipboard;
import net.yupol.transmissionremote.domain.model.Server;
import net.yupol.transmissionremote.model.json.Torrent;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends BaseMvpActivity<MainActivityView, MainActivityPresenter> implements MainActivityView,
        TorrentListFragment.OnTorrentSelectedListener, TorrentListFragment.ContextualActionBarListener {

    private static final String KEY_DRAWER_SERVER_LIST_EXPANDED = "key_drawer_server_list_expanded";
    private static final String KEY_SEARCH_ACTION_EXPANDED = "key_search_action_expanded";
    private static final String KEY_SEARCH_QUERY = "key_search_query";
    private static final String KEY_HAS_TORRENT_LIST = "has_torrent_list";
    private static final String KEY_FAB_EXPANDED = "key_fab_expanded";

    private static final int DRAWER_ITEM_ID_SETTINGS = 101;
    private static final int DRAWER_ITEM_FREE_SPACE = 102;

    private TransmissionRemote application;

    private ActionBarNavigationAdapter toolbarSpinnerAdapter;
    private Toolbar bottomToolbar;
    private Drawer drawer;
    private HeaderView headerView;

    private MenuItem bottomBarDownSpeedMenuItem;
    private MenuItem bottomBarUpSpeedMenuItem;

    private SearchView searchView;
    private MenuItem searchMenuItem;
    private boolean restoredSearchMenuItemExpanded = false;
    private CharSequence restoredSearchQuery = "";

    private boolean hasTorrentList = false;

    private Spinner toolbarSpinner;
    private MainActivityBinding binding;
    private FreeSpaceFooterDrawerItem freeSpaceFooterDrawerItem;

    private TorrentAdapter adapter;

    @Inject Clipboard clipboard;
    @Inject Preferences preferences;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.error_layout) View errorLayout;
    @BindView(R.id.error_text) TextView errorText;
    @BindView(R.id.detailed_error_text) TextView detailedErrorText;
    @BindView(R.id.welcome_layout) View welcomeLayout;
    @BindView(R.id.add_torrent_button) View addTorrentFab;

    @Inject MainActivityPresenter injectedPresenter;

    @Nullable private ActionMode actionMode;

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_torrent_list_menu, menu);
            inflater.inflate(R.menu.torrent_actions_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_select_all:
                    presenter.selectAllClicked();
                    return true;
                case R.id.action_remove_torrents:
                    presenter.removeSelectedClicked();
                    return true;
                case R.id.action_start:
                    presenter.resumeAllClicked();
                    return true;
                case R.id.action_pause:
                    presenter.pauseSelectedClicked();
                    return true;
                case R.id.action_start_now:
                    presenter.startNowSelectedClicked();
                    return true;
                case R.id.action_rename:
                    presenter.renameSelectedClicked();
                    return true;
                case R.id.action_set_location:
                    presenter.setLocationForSelectedClicked();
                    return true;
                case R.id.action_verify:
                    presenter.verifySelectedClicked();
                    return true;
                case R.id.action_reannounce:
                    presenter.reannounceSelectedClicked();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            presenter.selectionModeFinished();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TransmissionRemote.getInstance().appComponent().inject(this);
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        ButterKnife.bind(this);

        application = TransmissionRemote.getApplication(this);
        clipboard = new Clipboard(application);

        swipeRefresh.setOnRefreshListener(presenter::refreshTorrentList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setItemAnimator(null);
        adapter = new TorrentAdapter(new TorrentAdapter.ClickListener() {
            @Override
            public void onPauseClicked(int torrentId) {
                presenter.pauseClicked(torrentId);
            }

            @Override
            public void onResumeClicked(int torrentId) {
                presenter.resumeClicked(torrentId);
            }

            @Override
            public void onTorrentClicked(int torrentId) {
                presenter.torrentClicked(torrentId);
            }

            @Override
            public boolean onTorrentLongClicked(int torrentId) {
                return presenter.torrentLongClicked(torrentId);
            }
        });
        recyclerView.setAdapter(adapter);

        detailedErrorText.setMovementMethod(new ScrollingMovementMethod());

        setupActionBar();
        setupBottomToolbar();
        setupDrawer();
        setupFloatingActionButton();
    }

    @NonNull
    @Override
    public MainActivityPresenter createPresenter() {
        return injectedPresenter;
    }

    @OnLongClick(R.id.detailed_error_text)
    boolean onErrorDetailsLongClicked() {
        clipboard.setPlainTextClip("Error text", detailedErrorText.getText());
        Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
        return true;
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
                    presenter.activeServerSelected(server);
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

        TurtleModeButton turtleModeButton = bottomToolbar.findViewById(R.id.turtle_mode_button);
        turtleModeButton.setEnableChangedListener(isEnabled -> {
            if (isEnabled == application.isSpeedLimitEnabled()) return;

        });

        bottomToolbar.inflateMenu(R.menu.bottom_toolbar_menu);
        Menu menu = bottomToolbar.getMenu();
        bottomBarDownSpeedMenuItem = menu.findItem(R.id.action_download_speed);
        bottomBarUpSpeedMenuItem = menu.findItem(R.id.action_upload_speed);
    }

    private void setupDrawer() {
        PrimaryDrawerItem settingsItem = new PrimaryDrawerItem().withName(R.string.action_settings)
                .withIdentifier(DRAWER_ITEM_ID_SETTINGS)
                .withIcon(GoogleMaterial.Icon.gmd_settings)
                .withSelectable(false);

        SwitchDrawerItem nightModeItem = new SwitchDrawerItem().withName(R.string.night_mode)
                .withIcon(CommunityMaterial.Icon2.cmd_theme_light_dark)
                .withSelectable(false)
                .withChecked(ThemeUtils.isInNightMode(this))
                .withOnCheckedChangeListener((drawerItem, buttonView, isChecked) -> switchTheme(isChecked));

        headerView = new HeaderView(this);
        headerView.setHeaderListener(new HeaderView.HeaderListener() {
            @Override
            public void onSettingsPressed() {
                startActivity(new Intent(MainActivity.this, ServerPreferencesActivity.class));
            }

            @Override
            public void onServerSelected(Server server) {
                presenter.activeServerSelected(server);
            }

            @Override
            public void onAddServerPressed() {
                presenter.addServerClicked();
            }

            @Override
            public void onManageServersPressed() {
                startActivity(new Intent(MainActivity.this, ServerListActivity.class));
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
        freeSpaceFooterDrawerItem.withIdentifier(DRAWER_ITEM_FREE_SPACE);

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
                        drawer.updateItem(selectedItem);
                        application.setSorting(selectedItem.getSortedBy(), sortOrder);
                    }
                }).build();

        headerView.setDrawer(drawer);

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

        binding.addTorrentByFileButton.setIconDrawable(
                new IconicsDrawable(this, CommunityMaterial.Icon.cmd_file_outline)
                        .paddingRes(R.dimen.fab_icon_padding)
                        .colorRes(R.color.text_primary_inverse));

        binding.addTorrentByMagnetButton.setIconDrawable(
                new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_magnet)
                        .paddingRes(R.dimen.fab_icon_padding)
                        .colorRes(R.color.text_primary_inverse));

        binding.addTorrentButton.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                binding.fabOverlay.setVisibility(VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                binding.fabOverlay.setVisibility(GONE);
            }
        });

        binding.fabOverlay.setOnClickListener(v -> binding.addTorrentButton.collapse());

        binding.fabOverlay.setVisibility(binding.addTorrentButton.isExpanded() ? VISIBLE : GONE);
    }

    private void switchTheme(boolean nightMode) {
        ThemeUtils.setIsInNightMode(this, nightMode);
        recreate();
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
        binding.fabOverlay.setVisibility(isFabExpanded ? VISIBLE : GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.torrent_list_menu, menu);

        final MenuItem downloadItem = menu.findItem(R.id.action_download_speed);
        if (downloadItem != null) {
            SpeedTextView downloadSpeedView = (SpeedTextView) downloadItem.getActionView();
        }
        final MenuItem uploadItem = menu.findItem(R.id.action_upload_speed);
        if (uploadItem != null) {
            SpeedTextView uploadSpeedView = (SpeedTextView) uploadItem.getActionView();
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
        // TODO: implement
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_turtle_mode:
                // TODO: implement
                return true;
            case R.id.action_open_torrent:
                // TODO: implement
                return true;
            case R.id.action_start_all_torrents:
                presenter.resumeAllClicked();
                return true;
            case R.id.action_pause_all_torrents:
                presenter.pauseAllClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
    public void onTorrentSelected(Torrent torrent) {
        Intent intent = new Intent(this, TorrentDetailsActivity.class);
        intent.putExtra(TorrentDetailsActivity.EXTRA_TORRENT, torrent);
        startActivity(intent);
    }

    @Override
    public void onCABOpen() {
        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onCABClose() {
        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    //region MainActivityView interface implementation

    @Override
    public void showWelcomeScreen() {
        welcomeLayout.setVisibility(VISIBLE);
        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void hideWelcomeScreen() {
        welcomeLayout.setVisibility(GONE);
        drawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void showLoading() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showTorrents(@NonNull List<TorrentViewModel> torrents) {
        adapter.setTorrents(torrents);
        recyclerView.setVisibility(VISIBLE);
    }

    @Override
    public void hideTorrents() {
        recyclerView.setVisibility(GONE);
    }

    @Override
    public void updateTorrents(@NonNull TorrentViewModel... torrents) {
        adapter.updateTorrents(torrents);
    }

    @Override
    public void updateTorrent(int torrentId) {
        adapter.updateTorrentWithId(torrentId);
    }

    @Override
    public void showError(@NonNull String summary, @Nullable String details) {
        Toast.makeText(this, summary, Toast.LENGTH_SHORT).show();

        binding.errorLayout.setVisibility(VISIBLE);
        errorText.setText(summary);
        detailedErrorText.setVisibility(details != null ? VISIBLE : GONE);
        if (details != null) detailedErrorText.setText(details);
    }

    @Override
    public void hideError() {
        errorLayout.setVisibility(GONE);
    }

    @Override
    public void showErrorAlert(@NonNull Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void serverListChanged(@NotNull List<Server> servers, @NotNull Server activeServer) {
        headerView.setServers(servers, activeServer);
        toolbarSpinnerAdapter.setServers(servers, activeServer);
    }

    @Override
    public void showFab() {
        addTorrentFab.setVisibility(VISIBLE);
    }

    @Override
    public void hideFab() {
        addTorrentFab.setVisibility(GONE);
    }

    @Override
    public void startSelection() {
        startSupportActionMode(actionModeCallback);
    }

    @Override
    public void finishSelection() {
        if (actionMode != null) actionMode.finish();
    }

    @Override
    public void setSelectionTitle(@NotNull String title) {
        if (actionMode != null) actionMode.setTitle(title);
    }

    @Override
    public void setGroupActionsEnabled(boolean enabled) {
        if (actionMode != null) {
            Menu menu = actionMode.getMenu();
            menu.findItem(R.id.action_remove_torrents).setEnabled(enabled);
            menu.findItem(R.id.action_pause).setEnabled(enabled);
            menu.findItem(R.id.action_start).setEnabled(enabled);
            menu.findItem(R.id.action_start_now).setEnabled(enabled);
            menu.findItem(R.id.action_rename).setEnabled(enabled);
            menu.findItem(R.id.action_set_location).setEnabled(enabled);
            menu.findItem(R.id.action_verify).setEnabled(enabled);
            menu.findItem(R.id.action_reannounce).setEnabled(enabled);
        }
    }

    @Override
    public void setRenameActionEnabled(boolean enabled) {
        if (actionMode != null) {
            actionMode.getMenu().findItem(R.id.action_rename).setEnabled(enabled);
        }
    }

    // endregion

    // region Routing

    @Override
    public void openAddServerScreen() {
        startActivity(new Intent(this, AddServerActivity.class));
    }

    @Override
    public void openServerSettings(@NonNull Server server) {
        startActivity(ServerDetailsActivity.intent(this, server.name));
    }

    @Override
    public void openNetworkSettings() {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @Override
    public void openTorrentDetails() {

    }

    // endregion

    //region Click listeners

    @OnClick(R.id.add_server_button)
    void onAddServerClicked() {
        presenter.addServerClicked();
    }

    @OnClick(R.id.server_settings_button)
    void onServerSettingsClicked() {
        presenter.serverSettingsClicked();
    }

    @OnClick(R.id.network_settings_button)
    void onNetworkSettingsClicked() {
        presenter.networkSettingsClicked();
    }

    @OnClick(R.id.retry_button)
    void onRetryButtonClicked() {
        presenter.retryButtonClicked();
    }
    // endregion
}
