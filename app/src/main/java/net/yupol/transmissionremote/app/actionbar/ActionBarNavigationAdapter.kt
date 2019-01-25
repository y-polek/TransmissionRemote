package net.yupol.transmissionremote.app.actionbar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.home.filter.FILTERS
import net.yupol.transmissionremote.app.home.filter.Filter
import net.yupol.transmissionremote.app.utils.ColorUtils.resolveColor
import net.yupol.transmissionremote.app.utils.Equals
import net.yupol.transmissionremote.domain.model.Server
import java.util.*

class ActionBarNavigationAdapter(context: Context) : BaseAdapter() {

    private val textColorPrimary: Int = resolveColor(context, android.R.attr.textColorPrimary, R.color.text_primary)
    private val accentColor: Int = resolveColor(context, R.attr.colorAccent, R.color.accent)
    private val alternativeAccentColor: Int = context.resources.getColor(R.color.alternative_accent)
    private val textColorPrimaryInverse: Int = resolveColor(context, android.R.attr.textColorPrimaryInverse, R.color.text_primary_inverse)

    private var servers = emptyList<Server>()
    private var activeServer: Server? = null

    var activeFilter: Filter = FILTERS.first()
    var counts: Map<Filter, Int> = emptyMap()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
        when (id) {
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
        return when {
            position == 0 -> ID_SERVER_TITLE
            position <= servers.size -> ID_SERVER
            position == servers.size + 1 -> ID_FILTER_TITLE
            else -> ID_FILTER
        }
    }

    override fun isEnabled(position: Int): Boolean {
        val id = getItemId(position)
        return id == ID_SERVER || id == ID_FILTER
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.drop_down_navigation_item, parent, false)

        val text = itemView.findViewById<TextView>(R.id.text)
        val countText = itemView.findViewById<TextView>(R.id.torrent_count)
        val headerText = itemView.findViewById<TextView>(R.id.header_text)
        val separator = itemView.findViewById<View>(R.id.separator)

        val id = getItemId(position)
        if (id == ID_SERVER_TITLE || id == ID_FILTER_TITLE) {
            text.visibility = GONE
            countText.visibility = GONE
            headerText.visibility = VISIBLE
            separator.visibility = VISIBLE

            headerText.setText(if (id == ID_SERVER_TITLE) R.string.servers else R.string.filters)
        } else {
            text.visibility = VISIBLE
            headerText.visibility = GONE
            separator.visibility = GONE

            if (id == ID_SERVER) {
                countText.visibility = GONE
                val server = getItem(position) as Server
                text.text = server.name
                text.setTextColor(dropDownTextColor(server == activeServer))
            } else if (id == ID_FILTER) {
                countText.visibility = VISIBLE
                val filter = getItem(position) as Filter
                text.setText(filter.name)
                countText.text = "${counts[filter] ?: 0}"

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
        val view = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.drop_down_navigation, parent, false)

        val serverName = view.findViewById<TextView>(R.id.server_name)
        serverName.text = activeServer?.name.orEmpty()
        serverName.setTextColor(textColorPrimaryInverse)

        val filterName = view.findViewById<TextView>(R.id.filter_name)
        filterName.setText(activeFilter.name)

        filterName.setTextColor(if (activeFilter == FILTERS.first()) textColorPrimaryInverse else alternativeAccentColor)

        return view
    }

    companion object {

        private val TAG = ActionBarNavigationAdapter::class.java.simpleName

        const val ID_SERVER = 0L
        const val ID_FILTER = 1L
        private const val ID_SERVER_TITLE = 2L
        private const val ID_FILTER_TITLE = 3L
    }
}
