package net.yupol.transmissionremote.app.torrentlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.torrent_list_layout.view.*
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.utils.DividerItemDecoration
import net.yupol.transmissionremote.model.json.Torrent

class TorrentListFragment2: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.torrent_list_layout, container, false)

        view.recyclerView.layoutManager = LinearLayoutManager(context)
        view.recyclerView.addItemDecoration(DividerItemDecoration(context))
        view.recyclerView.itemAnimator = null
        view.recyclerView.adapter = TorrentsAdapter()

        return view
    }

    fun search(query: String) {
        TODO()
    }

    fun closeSearch() {
        TODO()
    }

    interface OnTorrentSelectedListener {
        fun onTorrentSelected(torrent: Torrent)
    }

    interface ContextualActionBarListener {
        fun onCABOpen()
        fun onCABClose()
    }
}