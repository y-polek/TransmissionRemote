package net.yupol.transmissionremote.app.home

import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.yupol.transmissionremote.app.model.mapper.TorrentMapper
import net.yupol.transmissionremote.app.mvp.MvpViewCallback
import net.yupol.transmissionremote.domain.usecase.TorrentListInteractor

class MainActivityPresenter(
        private val interactor: TorrentListInteractor,
        private val torrentMapper: TorrentMapper): MvpNullObjectBasePresenter<MainActivityView>(), MvpViewCallback
{
    companion object {
        private const val TAG = "MainActivityPresenter"
    }

    private var torrentListDisposable: Disposable? = null

    override fun viewStarted() {
        view.showLoading()

        torrentListDisposable = interactor.loadTorrentList()
                .map(torrentMapper::toViewMode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ torrents ->
                    view.hideLoading()
                    view.showTorrents(torrents)
                }, { error ->
                    view.hideLoading()
                    view.showError(error)
                })
    }

    override fun viewStopped() {
        torrentListDisposable?.dispose()
    }

    fun pauseClicked(torrentId: Int) {
        val d = interactor.pauseTorrent(torrentId)
                .map(torrentMapper::toViewModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ torrent ->
                    view.showUpdatedTorrents(torrent)
                }, { error ->
                    view.showErrorAlert(error)
                })
    }

    fun resumeClicked(torrentId: Int) {
        val d = interactor.resumeTorrent(torrentId)
                .map(torrentMapper::toViewModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ torrent ->
                    view.showUpdatedTorrents(torrent)
                }, { error ->
                    view.showErrorAlert(error)
                })
    }
}
