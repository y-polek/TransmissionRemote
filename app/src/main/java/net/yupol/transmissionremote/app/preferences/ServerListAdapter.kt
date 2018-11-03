package net.yupol.transmissionremote.app.preferences

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.domain.model.Server

class ServerListAdapter(private val listener: OnServerSelectedListener): RecyclerView.Adapter<ServerListAdapter.ViewHolder>() {

    private var servers: List<Server> = emptyList()
    private var activeServer: Server? = null

    init {
        setHasStableIds(true)
    }

    fun setServers(servers: List<Server>, activeServer: Server?) {
        this.servers = arrayListOf(*servers.toTypedArray())
        this.activeServer = activeServer
        notifyDataSetChanged()
    }

    override fun getItemCount() = servers.size

    override fun getItemId(position: Int): Long {
        return servers[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.server_item_layout, parent, false)
        return ViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == RecyclerView.NO_POSITION) return
        val server = servers[position]
        holder.bind(server, server == activeServer)
    }

    class ViewHolder(itemView: View, private val listener: OnServerSelectedListener): RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.name) lateinit var name: TextView
        @BindView(R.id.address) lateinit var address: TextView
        @BindView(R.id.radio_button) lateinit var radioButton: RadioButton

        private lateinit var server: Server

        init {
            ButterKnife.bind(this, itemView)
        }

        @SuppressLint("SetTextI18n")
        fun bind(server: Server, isActive: Boolean) {
            this.server = server

            name.text = server.name

            val protocol = if (server.https) "https" else "http"
            val host = server.host.value
            val portText = if (server.port != null) ":${server.port}" else ""
            address.text = "$protocol//$host$portText"

            radioButton.isChecked = isActive
        }

        @OnClick(R.id.root_layout, R.id.radio_button)
        fun onServerClicked() {
            listener.onServerSelected(server)

        }
    }

    interface OnServerSelectedListener {
        fun onServerSelected(server: Server)
    }
}
