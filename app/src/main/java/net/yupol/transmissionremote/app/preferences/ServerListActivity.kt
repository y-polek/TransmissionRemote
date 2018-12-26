package net.yupol.transmissionremote.app.preferences

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import net.yupol.transmissionremote.app.BaseActivity
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.server.AddServerActivity
import net.yupol.transmissionremote.app.server.ServerDetailsActivity
import net.yupol.transmissionremote.app.utils.DividerItemDecoration
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.repository.ServerListRepository
import javax.inject.Inject

class ServerListActivity : BaseActivity(), ServerListAdapter.OnServerSelectedListener {

    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView
    @BindView(R.id.empty_view) lateinit var emptyView: View

    @Inject lateinit var repo: ServerListRepository

    private lateinit var adapter: ServerListAdapter
    private lateinit var serversSubscription: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        TransmissionRemote.getInstance().appComponent().inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.server_list_activity)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this))
        adapter = ServerListAdapter(this)
        recyclerView.adapter = adapter

        serversSubscription = Observable.combineLatest(repo.servers(), repo.activeServer(), BiFunction { servers: List<Server>, activeServer: Server -> servers to activeServer })
                .subscribe { (servers, activeServer) ->
                    adapter.setServers(servers, activeServer)

                    emptyView.visibility = if (servers.isEmpty()) View.VISIBLE else View.GONE
                }
    }

    override fun onDestroy() {
        serversSubscription.dispose()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.servers_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            openNewServerActivity()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onServerSelected(server: Server) {
        repo.setActiveServer(server)
        startActivity(ServerDetailsActivity.intent(this, server.name))
    }

    private fun openNewServerActivity() {
        startActivity(AddServerActivity.intent(this))
    }
}
