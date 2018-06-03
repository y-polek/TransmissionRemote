package net.yupol.transmissionremote.app.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.torrent_list_layout.view.*
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.torrentlist.TorrentsAdapter
import net.yupol.transmissionremote.app.torrentlist.TorrentsRepository
import net.yupol.transmissionremote.app.utils.DividerItemDecoration
import net.yupol.transmissionremote.model.json.Torrent
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TorrentListFragment2: Fragment() {

    companion object {
        fun newInstance() = TorrentListFragment2()
    }

    @Inject lateinit var repository: TorrentsRepository
    lateinit var adapter: TorrentsAdapter

    private var torrentListSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        TransmissionRemote.getInstance().di.networkComponent?.inject(this)
        super.onCreate(savedInstanceState)

        adapter = TorrentsAdapter()
    }

    override fun onStart() {
        super.onStart()

        Timber.tag("TorrentsList").d("Subscribing")
        torrentListSubscription = repository.torrents()
                .subscribeOn(Schedulers.io())
                .retryWhen { handler ->
                    handler.flatMap { error ->
                        Observable.just("").delay(1, TimeUnit.SECONDS)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ torrents ->
                    Timber.tag("TorrentsList").d("Fragment onNext: $torrents")
                    adapter.torrents = torrents
                }, { error ->
                    Timber.tag("TorrentsList").d("onError: ${error.message}")
                })
    }

    override fun onStop() {
        Timber.tag("TorrentsList").d("Unsubscribing")
        torrentListSubscription?.dispose()
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.torrent_list_layout, container, false)

        view.recyclerView.layoutManager = LinearLayoutManager(context)
        view.recyclerView.addItemDecoration(DividerItemDecoration(context))
        view.recyclerView.itemAnimator = null
        view.recyclerView.adapter = adapter

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