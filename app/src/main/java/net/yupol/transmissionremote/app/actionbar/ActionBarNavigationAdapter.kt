package net.yupol.transmissionremote.app.actionbar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.home.filter.Filter
import net.yupol.transmissionremote.app.utils.ColorUtils.resolveColor
import net.yupol.transmissionremote.app.utils.Equals
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.model.Torrent
import java.util.*

class ActionBarNavigationAdapter(context: Context) : BaseAdapter() {

    private val textColorPrimary: Int = resolveColor(context, android.R.attr.textColorPrimary, R.color.text_primary)
    private val accentColor: Int = resolveColor(context, R.attr.colorAccent, R.color.accent)
    private val alternativeAccentColor: Int = context.resources.getColor(R.color.alternative_accent)
    private val textColorPrimaryInverse: Int = resolveColor(context, android.R.attr.textColorPrimaryInverse, R.color.text_primary_inverse)

    private var servers = emptyList<Server>()
    private var activeServer: Server? = null

    var activeFilter: Filter = FILTERS.first()

    fun setServers(servers: List<Server>, activeServer: Server?) {
        if (this.servers == servers && Equals.equals(activeServer, this.activeServer)) return
        this.servers = ArrayList(servers)
        this.activeServer = activeServer
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        // server title + servers + filter title + filters
        return 1 + servers.size + 1 + FILTERS.size
    }

    override fun getItem(position: Int): Any? {
        val id = getItemId(position)
        when (id.toInt()) {
            ID_SERVER_TITLE, ID_FILTER_TITLE -> return null
            ID_SERVER -> return servers[position - 1]
            ID_FILTER -> return FILTERS[position - servers.size - 2]
        }
        Log.e(TAG, "Unknown item at position $position" +
                ". Number of servers: ${servers.size}" +
                ", number of filters: ${FILTERS.size}")
        return null
    }

    override fun getItemId(position: Int): Long {
        if (position == 0) return ID_SERVER_TITLE.toLong()
        if (position <= servers.size) return ID_SERVER.toLong()
        return if (position == servers.size + 1) ID_FILTER_TITLE.toLong() else ID_FILTER.toLong()
    }

    override fun isEnabled(position: Int): Boolean {
        val id = getItemId(position)
        return id == ID_SERVER.toLong() || id == ID_FILTER.toLong()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val id = getItemId(position)
        val itemView = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.drop_down_navigation_item, parent, false)

        val text = itemView.findViewById<TextView>(R.id.text)
        val countText = itemView.findViewById<TextView>(R.id.torrent_count)
        val headerText = itemView.findViewById<TextView>(R.id.header_text)
        val separator = itemView.findViewById<View>(R.id.separator)

        if (id == ID_SERVER_TITLE.toLong() || id == ID_FILTER_TITLE.toLong()) {
            text.visibility = View.GONE
            countText.visibility = View.GONE
            headerText.visibility = View.VISIBLE
            separator.visibility = View.VISIBLE

            headerText.setText(if (id == ID_SERVER_TITLE.toLong()) R.string.servers else R.string.filters)
        } else {
            text.visibility = View.VISIBLE
            headerText.visibility = View.GONE
            separator.visibility = View.GONE

            if (id == ID_SERVER.toLong()) {
                countText.visibility = View.GONE
                val server = getItem(position) as Server?
                text.text = server!!.name
                text.setTextColor(dropDownTextColor(server == activeServer))
            } else if (id == ID_FILTER.toLong()) {
                countText.visibility = View.VISIBLE
                val filter = getItem(position) as Filter
                text.setText(filter.name)
                countText.text = "???"//FluentIterable.from<Torrent>(app.torrents).filter(filter).size().toString()

                val textColor = dropDownTextColor(filter == activeFilter)
                text.setTextColor(textColor)
                countText.setTextColor(textColor)
            }
        }

        return itemView
    }

    private fun dropDownTextColor(isActive: Boolean): Int {
        return if (isActive) accentColor else textColorPrimary
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (convertView == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.drop_down_navigation, parent, false)
        }

        val serverName = view!!.findViewById<TextView>(R.id.server_name)
        serverName.text = if (activeServer != null) activeServer!!.name else ""
        serverName.setTextColor(textColorPrimaryInverse)

        val filterName = view.findViewById<TextView>(R.id.filter_name)
        filterName.setText(activeFilter.name)

        filterName.setTextColor(if (activeFilter == FILTERS.first()) textColorPrimaryInverse else alternativeAccentColor)

        return view
    }

    companion object {

        private val TAG = ActionBarNavigationAdapter::class.java.simpleName

        const val ID_SERVER = 0
        const val ID_FILTER = 1
        private const val ID_SERVER_TITLE = 2
        private const val ID_FILTER_TITLE = 3

        private val FILTERS = listOf(
                Filter(R.string.filter_all) { true },
                Filter(R.string.filter_active, Torrent::isActive),
                Filter(R.string.filter_downloading, Torrent::isDownloading),
                Filter(R.string.filter_seeding, Torrent::isSeeding),
                Filter(R.string.filter_paused, Torrent::isPaused),
                Filter(R.string.filter_download_completed, Torrent::isCompleted),
                Filter(R.string.filter_finished, Torrent::isFinished)
        )
    }
}
