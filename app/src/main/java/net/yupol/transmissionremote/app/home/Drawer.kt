package net.yupol.transmissionremote.app.home

import android.app.Activity
import android.support.v7.widget.Toolbar
import android.view.View
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SectionDrawerItem
import com.mikepenz.materialdrawer.model.SwitchDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.drawer.FreeSpaceFooterDrawerItem
import net.yupol.transmissionremote.app.drawer.HeaderView
import net.yupol.transmissionremote.app.drawer.SortDrawerItem
import net.yupol.transmissionremote.app.sorting.SortOrder
import net.yupol.transmissionremote.app.sorting.SortedBy
import net.yupol.transmissionremote.app.utils.ThemeUtils
import net.yupol.transmissionremote.model.Server

class Drawer(val listener: Listener) {

    companion object {
        private const val DRAWER_ITEM_ID_SETTINGS = 101
    }

    private lateinit var drawer: Drawer

    fun setupDrawer(
            activity: Activity,
            toolbar: Toolbar,
            servers: List<Server>,
            activeServer: Server?,
            initSortedBy: SortedBy,
            initSortOrder: SortOrder)
    {
        val settingsItem = PrimaryDrawerItem().withName(R.string.action_settings)
                .withIdentifier(DRAWER_ITEM_ID_SETTINGS)
                .withIcon(GoogleMaterial.Icon.gmd_settings)
                .withSelectable(false)

        val nightModeItem = SwitchDrawerItem().withName(R.string.night_mode)
                .withIcon(CommunityMaterial.Icon.cmd_theme_light_dark)
                .withSelectable(false)
                .withChecked(ThemeUtils.isInNightMode(activity))
                .withOnCheckedChangeListener { _, _, isChecked -> listener.onThemeSwitched(isChecked) }

        val headerView = HeaderView(activity)
        headerView.setHeaderListener(object : HeaderView.HeaderListener {
            override fun onSettingsPressed() {
                listener.onServerSettingsPressed()
            }

            override fun onServerSelected(server: Server) {
                listener.onServerSelected(server)
            }

            override fun onAddServerPressed() {
                listener.onAddServerPressed()
            }

            override fun onManageServersPressed() {
                listener.onManageServersPressed()
            }
        })

        val sortItems = arrayOf(
                SortDrawerItem(SortedBy.NAME).withName(R.string.drawer_sort_by_name),
                SortDrawerItem(SortedBy.DATE_ADDED).withName(R.string.drawer_sort_by_date_added),
                SortDrawerItem(SortedBy.SIZE).withName(R.string.drawer_sort_by_size),
                SortDrawerItem(SortedBy.TIME_REMAINING).withName(R.string.drawer_sort_by_time_remaining),
                SortDrawerItem(SortedBy.PROGRESS).withName(R.string.drawer_sort_by_progress),
                SortDrawerItem(SortedBy.QUEUE_POSITION).withName(R.string.drawer_sort_by_queue_position),
                SortDrawerItem(SortedBy.UPLOAD_RATIO).withName(R.string.drawer_sort_by_upload_ratio))

        val freeSpaceFooterDrawerItem = FreeSpaceFooterDrawerItem()
        freeSpaceFooterDrawerItem.withSelectable(false)

        drawer = DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withHeader(headerView)
                .addDrawerItems(SectionDrawerItem().withName(R.string.drawer_sort_by).withDivider(false))
                .addDrawerItems(*sortItems)
                .addStickyDrawerItems(
                        nightModeItem,
                        settingsItem,
                        freeSpaceFooterDrawerItem
                )
                .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view: View, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                        return when {
                            drawerItem is SortDrawerItem -> {
                                handleSortItemClick(drawerItem)
                                true
                            }
                            drawerItem.identifier == DRAWER_ITEM_ID_SETTINGS -> {
                                listener.onSettingsPressed()
                                false
                            }
                            else -> false
                        }
                    }

                    private fun handleSortItemClick(selectedItem: SortDrawerItem) {
                        val prevSortOrder = selectedItem.sortOrder
                        val sortOrder: SortOrder
                        sortOrder = when (prevSortOrder) {
                            SortOrder.ASCENDING -> SortOrder.DESCENDING
                            else -> SortOrder.ASCENDING
                        }
                        for (item in sortItems) {
                            if (item !== selectedItem) {
                                item.sortOrder = null
                                item.withSetSelected(false)
                                drawer.updateItem(item)
                            }
                        }
                        selectedItem.sortOrder = sortOrder
                        listener.onSortingChanged(selectedItem.sortedBy, sortOrder)
                    }
                }).build()

        headerView.setDrawer(drawer)

        headerView.setServers(servers, servers.indexOf(activeServer))

        for (item in sortItems) {
            if (item.sortedBy == initSortedBy) {
                item.sortOrder = initSortOrder
                item.withSetSelected(true)
                break
            }
        }
    }

    interface Listener {
        fun onServerSettingsPressed()
        fun onAddServerPressed()
        fun onManageServersPressed()
        fun onServerSelected(server: Server)
        fun onSettingsPressed()
        fun onSortingChanged(sortedBy: SortedBy, sortOrder: SortOrder)
        fun onThemeSwitched(nightMode: Boolean)
    }
}