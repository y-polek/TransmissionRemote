package net.yupol.transmissionremote.app.home

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.home_activity.*
import net.yupol.transmissionremote.app.BaseActivity
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.preferences.PreferencesActivity
import net.yupol.transmissionremote.app.preferences.ServerPreferencesActivity
import net.yupol.transmissionremote.app.preferences.ServersActivity
import net.yupol.transmissionremote.app.server.AddServerActivity
import net.yupol.transmissionremote.app.server.ServersRepository
import net.yupol.transmissionremote.app.sorting.SortOrder
import net.yupol.transmissionremote.app.sorting.SortedBy
import net.yupol.transmissionremote.app.torrentlist.EmptyServerFragment
import net.yupol.transmissionremote.app.utils.ThemeUtils
import net.yupol.transmissionremote.model.Server
import javax.inject.Inject

class HomeActivity: BaseActivity(), Drawer.Listener, EmptyServerFragment.OnAddServerClickListener {

    companion object {
        const val REQUEST_CODE_SERVER_PARAMS = 1

        const val FRAGMENT_TAG_TORRENT_LIST = "fragment_tag_torrent_list"
        const val FRAGMENT_TAG_NO_SERVER = "fragment_tag_no_server"
    }

    private lateinit var app: TransmissionRemote
    @Inject lateinit var serversRepository: ServersRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        app = TransmissionRemote.getInstance()
        app.di.applicationComponent.inject(this)

        serversRepository.getServers().observe(this, Observer { servers ->
            servers ?: return@Observer


        })

        LivedataUti

        val drawer = Drawer(this, serversRepository)
        val servers = serversRepository.getServers().value ?: listOf()
        val activeServer = serversRepository.getActiveServer().value
        drawer.setupDrawer(this, toolbar, servers, activeServer, app.sortedBy, app.sortOrder)

        if (activeServer != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, TorrentListFragment2.newInstance(), FRAGMENT_TAG_TORRENT_LIST)
                    .commit()
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, EmptyServerFragment(), FRAGMENT_TAG_NO_SERVER)
                    .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SERVER_PARAMS -> {
                if (resultCode == RESULT_OK && data != null) {
                    val server = data.getParcelableExtra<Server>(AddServerActivity.EXTRA_SEVER)
                    serversRepository.addServer(server)
                    serversRepository.setActiveServer(server)
                }
            }
        }
    }

    override fun onServerSettingsPressed() {
        startActivity(Intent(this, ServerPreferencesActivity::class.java))
    }

    override fun onAddServerPressed() {
        val intent = Intent(this, AddServerActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_SERVER_PARAMS)
    }

    override fun onManageServersPressed() {
        startActivity(Intent(this, ServersActivity::class.java))
    }

    override fun onServerSelected(server: Server) {
        TODO("not implemented")
    }

    override fun onSettingsPressed() {
        startActivity(Intent(this, PreferencesActivity::class.java))
    }

    override fun onSortingChanged(sortedBy: SortedBy, sortOrder: SortOrder) {
        app.setSorting(sortedBy, sortOrder)
    }

    override fun onThemeSwitched(nightMode: Boolean) {
        ThemeUtils.setIsInNightMode(this, nightMode)
        recreate()
    }

    override fun onAddServerButtonClicked() {
        startActivityForResult(AddServerActivity.intent(this), REQUEST_CODE_SERVER_PARAMS)
    }
}