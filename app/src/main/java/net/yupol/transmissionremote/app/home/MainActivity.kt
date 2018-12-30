package net.yupol.transmissionremote.app.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.view.LayoutInflaterCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.context.IconicsLayoutInflater2
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SectionDrawerItem
import com.mikepenz.materialdrawer.model.SwitchDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import net.yupol.transmissionremote.app.BaseMvpActivity
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.actionbar.ActionBarNavigationAdapter
import net.yupol.transmissionremote.app.actionbar.SpeedTextView
import net.yupol.transmissionremote.app.actionbar.TurtleModeButton
import net.yupol.transmissionremote.app.databinding.MainActivityBinding
import net.yupol.transmissionremote.app.dialogs.RenameTorrentDialog
import net.yupol.transmissionremote.app.drawer.FreeSpaceFooterDrawerItem
import net.yupol.transmissionremote.app.drawer.HeaderView
import net.yupol.transmissionremote.app.drawer.SortDrawerItem
import net.yupol.transmissionremote.app.filtering.Filter
import net.yupol.transmissionremote.app.model.TorrentViewModel
import net.yupol.transmissionremote.app.preferences.Preferences
import net.yupol.transmissionremote.app.preferences.PreferencesActivity
import net.yupol.transmissionremote.app.preferences.ServerListActivity
import net.yupol.transmissionremote.app.preferences.ServerPreferencesActivity
import net.yupol.transmissionremote.app.server.AddServerActivity
import net.yupol.transmissionremote.app.server.ServerDetailsActivity
import net.yupol.transmissionremote.app.sorting.SortOrder
import net.yupol.transmissionremote.app.sorting.SortedBy
import net.yupol.transmissionremote.app.torrentdetails.TorrentDetailsActivity
import net.yupol.transmissionremote.app.torrentlist.TorrentListFragment
import net.yupol.transmissionremote.app.utils.DividerItemDecoration
import net.yupol.transmissionremote.app.utils.IconUtils
import net.yupol.transmissionremote.app.utils.ThemeUtils
import net.yupol.transmissionremote.device.clipboard.Clipboard
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.model.json.Torrent
import javax.inject.Inject

class MainActivity : BaseMvpActivity<MainActivityView, MainActivityPresenter>(),
        MainActivityView,
        TorrentListFragment.OnTorrentSelectedListener,
        TorrentListFragment.ContextualActionBarListener,
        RenameTorrentDialog.Listener
{
    private var application: TransmissionRemote? = null

    private var toolbarSpinnerAdapter: ActionBarNavigationAdapter? = null
    private lateinit var drawer: Drawer
    private var headerView: HeaderView? = null

    private var bottomBarDownSpeedMenuItem: MenuItem? = null
    private var bottomBarUpSpeedMenuItem: MenuItem? = null

    private var searchView: SearchView? = null
    private var searchMenuItem: MenuItem? = null
    private var restoredSearchMenuItemExpanded = false
    private var restoredSearchQuery: CharSequence = ""

    private lateinit var toolbarSpinner: Spinner
    private lateinit var binding: MainActivityBinding
    private var freeSpaceFooterDrawerItem: FreeSpaceFooterDrawerItem? = null

    private lateinit var adapter: TorrentAdapter

    @Inject lateinit var clipboard: Clipboard
    @Inject lateinit var preferences: Preferences

    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView
    @BindView(R.id.swipe_refresh) lateinit var swipeRefresh: SwipeRefreshLayout
    @BindView(R.id.error_layout) lateinit var errorLayout: View
    @BindView(R.id.error_text) lateinit var errorText: TextView
    @BindView(R.id.detailed_error_text) lateinit var detailedErrorText: TextView
    @BindView(R.id.welcome_layout) lateinit var welcomeLayout: View
    @BindView(R.id.add_torrent_button) lateinit var addTorrentFab: View
    @BindView(R.id.bottom_toolbar) @JvmField var bottomToolbar: Toolbar? = null
    @BindView(R.id.turtle_mode_button) @JvmField var turtleModeButton: TurtleModeButton? = null

    private var turtleModeMenu: MenuItem? = null

    private var downloadSpeedView: SpeedTextView? = null
    private var uploadSpeedView: SpeedTextView? = null

    @Inject lateinit var injectedPresenter: MainActivityPresenter

    private var actionMode: ActionMode? = null

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            actionMode = mode
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.context_torrent_list_menu, menu)
            inflater.inflate(R.menu.torrent_actions_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_select_all -> {
                    presenter.selectAllClicked()
                    return true
                }
                R.id.action_remove_torrents -> {
                    presenter.removeSelectedClicked()
                    return true
                }
                R.id.action_start -> {
                    presenter.resumeSelectedClicked()
                    return true
                }
                R.id.action_pause -> {
                    presenter.pauseSelectedClicked()
                    return true
                }
                R.id.action_start_now -> {
                    presenter.startNowSelectedClicked()
                    return true
                }
                R.id.action_rename -> {
                    presenter.renameSelectedClicked()
                    return true
                }
                R.id.action_set_location -> {
                    presenter.setLocationForSelectedClicked()
                    return true
                }
                R.id.action_verify -> {
                    presenter.verifySelectedClicked()
                    return true
                }
                R.id.action_reannounce -> {
                    presenter.reannounceSelectedClicked()
                    return true
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            presenter.selectionModeFinished()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TransmissionRemote.getInstance().appComponent().inject(this)
        LayoutInflaterCompat.setFactory2(layoutInflater, IconicsLayoutInflater2(delegate))
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        ButterKnife.bind(this)

        application = TransmissionRemote.getApplication(this)
        clipboard = Clipboard(application!!)

        swipeRefresh.setOnRefreshListener(presenter::refreshTorrentList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this))
        recyclerView.itemAnimator = null
        adapter = TorrentAdapter(object : TorrentAdapter.ClickListener {
            override fun onPauseClicked(torrentId: Int) {
                presenter.pauseClicked(torrentId)
            }

            override fun onResumeClicked(torrentId: Int) {
                presenter.resumeClicked(torrentId)
            }

            override fun onTorrentClicked(torrentId: Int) {
                presenter.torrentClicked(torrentId)
            }

            override fun onTorrentLongClicked(torrentId: Int): Boolean {
                return presenter.torrentLongClicked(torrentId)
            }
        })
        recyclerView.adapter = adapter

        detailedErrorText.movementMethod = ScrollingMovementMethod()

        setupActionBar()
        setupBottomToolbar()
        setupDrawer()
        setupFloatingActionButton()

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(KEY_IN_SELECTION_MODE, false)) {
                startSupportActionMode(actionModeCallback)
                presenter.selectionModeRestored()
            }
        }
    }

    override fun createPresenter(): MainActivityPresenter {
        return injectedPresenter
    }

    @OnLongClick(R.id.detailed_error_text)
    internal fun onErrorDetailsLongClicked(): Boolean {
        clipboard.setPlainTextClip("Error text", detailedErrorText.text)
        Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show()
        return true
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)

        val spinnerContainer = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, binding.toolbar, false)
        val lp = Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        binding.toolbar.addView(spinnerContainer, lp)
        toolbarSpinner = spinnerContainer.findViewById(R.id.toolbar_spinner)
        toolbarSpinnerAdapter = ActionBarNavigationAdapter(this)
        toolbarSpinner.adapter = toolbarSpinnerAdapter
        toolbarSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.e("MainActivity", "onItemSelected")
                if (id == ActionBarNavigationAdapter.ID_SERVER.toLong()) {
                    val server = toolbarSpinnerAdapter!!.getItem(position) as Server
                    presenter.activeServerSelected(server)
                } else if (id == ActionBarNavigationAdapter.ID_FILTER.toLong()) {
                    val filter = toolbarSpinnerAdapter!!.getItem(position) as Filter
                    if (filter != application!!.activeFilter) {
                        application!!.activeFilter = filter
                    }
                }
                toolbarSpinnerAdapter!!.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupBottomToolbar() {
        if (bottomToolbar == null) return

        bottomToolbar!!.inflateMenu(R.menu.bottom_toolbar_menu)
        val menu = bottomToolbar!!.menu
        bottomBarDownSpeedMenuItem = menu.findItem(R.id.action_download_speed)
        bottomBarUpSpeedMenuItem = menu.findItem(R.id.action_upload_speed)
        downloadSpeedView = bottomBarDownSpeedMenuItem?.actionView as SpeedTextView?
        uploadSpeedView = bottomBarUpSpeedMenuItem?.actionView as SpeedTextView?
    }

    private fun setupDrawer() {
        val settingsItem = PrimaryDrawerItem().withName(R.string.action_settings)
                .withIdentifier(DRAWER_ITEM_ID_SETTINGS.toLong())
                .withIcon(GoogleMaterial.Icon.gmd_settings)
                .withSelectable(false)

        val nightModeItem = SwitchDrawerItem().withName(R.string.night_mode)
                .withIcon(CommunityMaterial.Icon2.cmd_theme_light_dark)
                .withSelectable(false)
                .withChecked(ThemeUtils.isInNightMode(this))
                .withOnCheckedChangeListener { _, _, isChecked ->
                    switchTheme(isChecked)
                }

        headerView = HeaderView(this)
        headerView!!.setHeaderListener(object : HeaderView.HeaderListener {
            override fun onSettingsPressed() {
                startActivity(Intent(this@MainActivity, ServerPreferencesActivity::class.java))
            }

            override fun onServerSelected(server: Server) {
                presenter.activeServerSelected(server)
            }

            override fun onAddServerPressed() {
                presenter.addServerClicked()
            }

            override fun onManageServersPressed() {
                startActivity(Intent(this@MainActivity, ServerListActivity::class.java))
            }
        })

        val sortItems = arrayOf(SortDrawerItem(SortedBy.NAME).withName(R.string.drawer_sort_by_name), SortDrawerItem(SortedBy.DATE_ADDED).withName(R.string.drawer_sort_by_date_added), SortDrawerItem(SortedBy.SIZE).withName(R.string.drawer_sort_by_size), SortDrawerItem(SortedBy.TIME_REMAINING).withName(R.string.drawer_sort_by_time_remaining), SortDrawerItem(SortedBy.PROGRESS).withName(R.string.drawer_sort_by_progress), SortDrawerItem(SortedBy.QUEUE_POSITION).withName(R.string.drawer_sort_by_queue_position), SortDrawerItem(SortedBy.UPLOAD_RATIO).withName(R.string.drawer_sort_by_upload_ratio))

        freeSpaceFooterDrawerItem = FreeSpaceFooterDrawerItem()
        freeSpaceFooterDrawerItem!!.withSelectable(false)
        freeSpaceFooterDrawerItem!!.withIdentifier(DRAWER_ITEM_FREE_SPACE.toLong())

        drawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(binding.toolbar)
                .withHeader(headerView!!)
                .addDrawerItems(SectionDrawerItem().withName(R.string.drawer_sort_by).withDivider(false))
                .addDrawerItems(*sortItems as Array<IDrawerItem<*, *>>)
                .addStickyDrawerItems(
                        nightModeItem,
                        settingsItem,
                        freeSpaceFooterDrawerItem
                )
                .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view: View, position: Int, drawerItem: IDrawerItem<*, *>): Boolean {
                        if (drawerItem is SortDrawerItem) {
                            handleSortItemClick(drawerItem)
                            return true
                        } else if (drawerItem.identifier == DRAWER_ITEM_ID_SETTINGS.toLong()) {
                            startActivity(Intent(this@MainActivity, PreferencesActivity::class.java))
                        }

                        return false
                    }

                    private fun handleSortItemClick(selectedItem: SortDrawerItem) {
                        val prevSortOrder = selectedItem.sortOrder
                        val sortOrder: SortOrder
                        if (prevSortOrder == null)
                            sortOrder = SortOrder.ASCENDING
                        else
                            sortOrder = if (prevSortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING else SortOrder.ASCENDING
                        for (item in sortItems) {
                            if (item !== selectedItem) {
                                item.sortOrder = null
                                item.withSetSelected(false)
                                drawer.updateItem(item)
                            }
                        }
                        selectedItem.sortOrder = sortOrder
                        drawer.updateItem(selectedItem)
                        application!!.setSorting(selectedItem.sortedBy, sortOrder)
                    }
                }).build()

        headerView!!.setDrawer(drawer)

        val persistedSortedBy = application!!.sortedBy
        val persistedSortOrder = application!!.sortOrder
        for (item in sortItems) {
            if (item.sortedBy == persistedSortedBy) {
                item.sortOrder = persistedSortOrder
                item.withSetSelected(true)
                break
            }
        }
    }

    private fun setupFloatingActionButton() {

        binding.addTorrentByFileButton.setIconDrawable(
                IconicsDrawable(this, CommunityMaterial.Icon.cmd_file_outline)
                        .paddingRes(R.dimen.fab_icon_padding)
                        .colorRes(R.color.text_primary_inverse))

        binding.addTorrentByMagnetButton.setIconDrawable(
                IconicsDrawable(this, CommunityMaterial.Icon2.cmd_magnet)
                        .paddingRes(R.dimen.fab_icon_padding)
                        .colorRes(R.color.text_primary_inverse))

        binding.addTorrentButton.setOnFloatingActionsMenuUpdateListener(object : FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
            override fun onMenuExpanded() {
                binding.fabOverlay.visibility = VISIBLE
            }

            override fun onMenuCollapsed() {
                binding.fabOverlay.visibility = GONE
            }
        })

        binding.fabOverlay.setOnClickListener { binding.addTorrentButton.collapse() }

        binding.fabOverlay.visibility = if (binding.addTorrentButton.isExpanded) VISIBLE else GONE
    }

    private fun switchTheme(nightMode: Boolean) {
        ThemeUtils.setIsInNightMode(this, nightMode)
        recreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_IN_SELECTION_MODE, actionMode != null)

        outState.putBoolean(KEY_DRAWER_SERVER_LIST_EXPANDED, drawer.switchedDrawerContent())
        if (searchMenuItem != null) {
            outState.putBoolean(KEY_SEARCH_ACTION_EXPANDED, searchMenuItem!!.isActionViewExpanded)
            outState.putCharSequence(KEY_SEARCH_QUERY, searchView!!.query)
        } else {
            outState.putBoolean(KEY_SEARCH_ACTION_EXPANDED, restoredSearchMenuItemExpanded)
            outState.putCharSequence(KEY_SEARCH_QUERY, restoredSearchQuery)
        }

        outState.putBoolean(KEY_FAB_EXPANDED, binding.addTorrentButton.isExpanded)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        if (savedInstanceState.getBoolean(KEY_DRAWER_SERVER_LIST_EXPANDED, false)) {
            headerView!!.showServersList()
        }

        restoredSearchMenuItemExpanded = savedInstanceState.getBoolean(KEY_SEARCH_ACTION_EXPANDED, false)
        restoredSearchQuery = savedInstanceState.getCharSequence(KEY_SEARCH_QUERY, "")

        val isFabExpanded = savedInstanceState.getBoolean(KEY_FAB_EXPANDED, false)
        if (isFabExpanded) {
            binding.addTorrentButton.expand()
        } else {
            binding.addTorrentButton.collapseImmediately()
        }
        binding.fabOverlay.visibility = if (isFabExpanded) VISIBLE else GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.torrent_list_menu, menu)

        turtleModeMenu = menu.findItem(R.id.action_turtle_mode)
        updateTurtleModeMenu(presenter.turtleModeEnabled)


        val downloadSpeedMenu = menu.findItem(R.id.action_download_speed)
        val uploadSpeedMenu = menu.findItem(R.id.action_upload_speed)
        if (downloadSpeedMenu != null && uploadSpeedMenu != null) {
            downloadSpeedView = downloadSpeedMenu.actionView as SpeedTextView
            uploadSpeedView = uploadSpeedMenu.actionView as SpeedTextView
        }

        searchMenuItem = menu.findItem(R.id.action_search)
        if (searchMenuItem == null) {
            searchMenuItem = bottomToolbar!!.menu.findItem(R.id.action_search)
        }
        IconUtils.setMenuIcon(this, searchMenuItem, FontAwesome.Icon.faw_search)

        searchView = searchMenuItem!!.actionView as SearchView
        // iconifiedByDefault must be false to avoid closing SearchView by close button (close button only clears text)
        searchView!!.setIconifiedByDefault(false)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        // Workaround issue #47. Setting query hint in code to avoid crash in SearchView#updateQueryHint
        searchView!!.queryHint = getString(R.string.search_hing)

        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchMenuItem!!.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                if (bottomBarDownSpeedMenuItem != null) bottomBarDownSpeedMenuItem!!.isVisible = false
                if (bottomBarUpSpeedMenuItem != null) bottomBarUpSpeedMenuItem!!.isVisible = false

                searchView!!.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                searchView!!.setQuery("", false)

                if (bottomBarDownSpeedMenuItem != null) bottomBarDownSpeedMenuItem!!.isVisible = true
                if (bottomBarUpSpeedMenuItem != null) bottomBarUpSpeedMenuItem!!.isVisible = true

                searchView!!.clearFocus()

                return true
            }
        })

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                handleSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                handleSearch(newText)
                return false
            }
        })

        if (restoredSearchMenuItemExpanded) {
            searchMenuItem!!.expandActionView()
            searchView!!.setQuery(restoredSearchQuery, true)
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun updateTurtleModeMenu(enabled: Boolean) {
        turtleModeMenu?.setIcon(if (enabled) R.drawable.ic_turtle_active else R.drawable.ic_turtle_default)
    }

    private fun handleSearch(query: String) {
        // TODO: implement
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_turtle_mode -> {
                updateTurtleModeMenu(!presenter.turtleModeEnabled)
                presenter.turtleModeToggled()
                return true
            }
            R.id.action_open_torrent -> {
                // TODO: implement
                return true
            }
            R.id.action_start_all_torrents -> {
                presenter.resumeAllClicked()
                return true
            }
            R.id.action_pause_all_torrents -> {
                presenter.pauseAllClicked()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        when {
            drawer.isDrawerOpen -> drawer.closeDrawer()
            searchMenuItem!!.isActionViewExpanded -> searchMenuItem!!.collapseActionView()
            binding.addTorrentButton.isExpanded -> binding.addTorrentButton.collapse()
            else -> super.onBackPressed()
        }
    }

    override fun onTorrentSelected(torrent: Torrent) {
        val intent = Intent(this, TorrentDetailsActivity::class.java)
        intent.putExtra(TorrentDetailsActivity.EXTRA_TORRENT, torrent)
        startActivity(intent)
    }

    override fun onCABOpen() {
        drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onCABClose() {
        drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    //region MainActivityView interface implementation

    override fun showWelcomeScreen() {
        welcomeLayout.visibility = VISIBLE
        drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun hideWelcomeScreen() {
        welcomeLayout.visibility = GONE
        drawer.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun showLoading() {
        swipeRefresh.isRefreshing = true
    }

    override fun hideLoading() {
        swipeRefresh.isRefreshing = false
    }

    override fun showTorrents(torrents: List<TorrentViewModel>) {
        adapter.setTorrents(torrents)
        recyclerView.visibility = VISIBLE
    }

    override fun hideTorrents() {
        recyclerView.visibility = GONE
    }

    override fun updateTorrents(vararg torrents: TorrentViewModel) {
        adapter.updateTorrents(*torrents)
    }

    override fun updateTorrent(torrentId: Int) {
        adapter.updateTorrentWithId(torrentId)
    }

    override fun showError(summary: String, details: String?) {
        Toast.makeText(this, summary, Toast.LENGTH_SHORT).show()

        binding.errorLayout.visibility = VISIBLE
        errorText.text = summary
        detailedErrorText.visibility = if (details != null) VISIBLE else GONE
        if (details != null) detailedErrorText.text = details
    }

    override fun hideError() {
        errorLayout.visibility = GONE
    }

    override fun showErrorAlert(error: Throwable) {
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
    }

    override fun serverListChanged(servers: List<Server>, activeServer: Server) {
        headerView!!.setServers(servers, activeServer)
        toolbarSpinnerAdapter!!.setServers(servers, activeServer)
    }

    override fun showFab() {
        addTorrentFab.visibility = VISIBLE
    }

    override fun hideFab() {
        addTorrentFab.visibility = GONE
    }

    override fun startSelection() {
        startSupportActionMode(actionModeCallback)
    }

    override fun finishSelection() {
        if (actionMode != null) actionMode!!.finish()
    }

    override fun setSelectionTitle(title: String) {
        if (actionMode != null) actionMode!!.title = title
    }

    override fun setGroupActionsEnabled(enabled: Boolean) {
        if (actionMode != null) {
            val menu = actionMode!!.menu
            menu.findItem(R.id.action_remove_torrents).isEnabled = enabled
            menu.findItem(R.id.action_pause).isEnabled = enabled
            menu.findItem(R.id.action_start).isEnabled = enabled
            menu.findItem(R.id.action_start_now).isEnabled = enabled
            menu.findItem(R.id.action_rename).isEnabled = enabled
            menu.findItem(R.id.action_set_location).isEnabled = enabled
            menu.findItem(R.id.action_verify).isEnabled = enabled
            menu.findItem(R.id.action_reannounce).isEnabled = enabled
        }
    }

    override fun setRenameActionEnabled(enabled: Boolean) {
        if (actionMode != null) {
            actionMode!!.menu.findItem(R.id.action_rename).isEnabled = enabled
        }
    }

    override fun setTurtleModeEnabled(enabled: Boolean) {
        turtleModeButton?.isEnabled = enabled
        updateTurtleModeMenu(enabled)
    }

    override fun showLoadingSpeed(downloadSpeed: Long, uploadSpeed: Long) {
        downloadSpeedView?.setSpeed(downloadSpeed)
        uploadSpeedView?.setSpeed(uploadSpeed)
    }

    // endregion

    // region Routing

    override fun openAddServerScreen() {
        startActivity(Intent(this, AddServerActivity::class.java))
    }

    override fun openServerSettings(server: Server) {
        startActivity(ServerDetailsActivity.intent(this, server.name))
    }

    override fun openNetworkSettings() {
        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }

    override fun openTorrentDetails() {

    }

    override fun openRemoveTorrentOptionsDialog() {
        MaterialDialog(this).show {
            title(R.string.remove_selected_torrents)
            listItems(R.array.remove_torrents_entries) { _, index, text ->
                when (index) {
                    0 -> presenter.removeSelectedTorrentsFromListClicked()
                    1 -> presenter.removeSelectedTorrentsFromListAndDeleteDataClicked()
                    else -> throw NotImplementedError("Unknown option at index $index: '$text'")
                }
            }
        }
    }

    override fun openDeleteTorrentDataConfirmation() {
        MaterialDialog(this).show {
            message(R.string.remove_data_confirmation)
            positiveButton(R.string.delete) {
                presenter.deleteSelectedTorrentsDataConfirmed()
            }
            negativeButton(R.string.cancel)
        }
    }

    override fun openRenameTorrentDialog(torrent: TorrentViewModel) {
        RenameTorrentDialog.instance(torrent).show(supportFragmentManager, "RenameTorrent")
    }

    override fun onTorrentNameSelected(torrent: TorrentViewModel, newName: String) {
        presenter.renameTorrent(torrent, newName)
    }

    // endregion

    //region Click listeners

    @OnClick(R.id.add_server_button)
    internal fun onAddServerClicked() {
        presenter.addServerClicked()
    }

    @OnClick(R.id.server_settings_button)
    internal fun onServerSettingsClicked() {
        presenter.serverSettingsClicked()
    }

    @OnClick(R.id.network_settings_button)
    internal fun onNetworkSettingsClicked() {
        presenter.networkSettingsClicked()
    }

    @OnClick(R.id.retry_button)
    internal fun onRetryButtonClicked() {
        presenter.retryButtonClicked()
    }

    @Optional
    @OnClick(R.id.turtle_mode_button)
    internal fun onTurtleModeClicked() {
        turtleModeButton?.toggle()
        presenter.turtleModeToggled()
    }

    companion object {

        private const val KEY_IN_SELECTION_MODE = "key_in_selection_mode"
        private const val KEY_DRAWER_SERVER_LIST_EXPANDED = "key_drawer_server_list_expanded"
        private const val KEY_SEARCH_ACTION_EXPANDED = "key_search_action_expanded"
        private const val KEY_SEARCH_QUERY = "key_search_query"
        private const val KEY_FAB_EXPANDED = "key_fab_expanded"

        private const val DRAWER_ITEM_ID_SETTINGS = 101
        private const val DRAWER_ITEM_FREE_SPACE = 102
    }
    // endregion
}
