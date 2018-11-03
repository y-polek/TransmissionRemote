package net.yupol.transmissionremote.app.preferences

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import net.yupol.transmissionremote.app.BaseFragment
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.utils.DividerItemDecoration
import net.yupol.transmissionremote.app.utils.IconUtils
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.repository.ServerRepository
import javax.inject.Inject

class ServersFragment : BaseFragment(), ServerListAdapter.OnServerSelectedListener {

    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView
    @BindView(R.id.empty_view) lateinit var emptyView: View

    @Inject lateinit var repo: ServerRepository

    private lateinit var adapter: ServerListAdapter
    private var serversSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        TransmissionRemote.getInstance().appComponent().inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        adapter = ServerListAdapter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.server_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context))
        recyclerView.adapter = adapter

        subscribeToServers()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.servers_menu, menu)
        IconUtils.setMenuIcon(activity, menu, R.id.action_add, GoogleMaterial.Icon.gmd_add)
    }

    override fun onDestroy() {
        serversSubscription?.dispose()
        super.onDestroy()
    }

    override fun onServerSelected(server: Server) {
        repo.setActiveServer(server)
    }

    private fun subscribeToServers() {
        serversSubscription?.dispose()

        serversSubscription = Observable.combineLatest(repo.servers(), repo.activeServer(), BiFunction { servers: List<Server>, activeServer: Server -> servers to activeServer })
                .subscribe { (servers, activeServer) ->
                    adapter.setServers(servers, activeServer)

                    Toast.makeText(context, activeServer.name, Toast.LENGTH_SHORT).show()

                    emptyView.visibility = if (servers.isEmpty()) VISIBLE else GONE
                }
    }
}
