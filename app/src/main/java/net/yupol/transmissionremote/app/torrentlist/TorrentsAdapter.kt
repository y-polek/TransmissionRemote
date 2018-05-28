package net.yupol.transmissionremote.app.torrentlist

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.torrentlist.TorrentsAdapter.ViewHolder
import net.yupol.transmissionremote.model.json.Torrent
import net.yupol.transmissionremote.utils.inflater

class TorrentsAdapter: RecyclerView.Adapter<ViewHolder>() {

    private var torrents = listOf<Torrent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflater().inflate(R.layout.torrent_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == RecyclerView.NO_POSITION) return

        holder.bind(torrents[position])
    }

    override fun getItemCount() = torrents.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(torrent: Torrent) {

        }
    }
}