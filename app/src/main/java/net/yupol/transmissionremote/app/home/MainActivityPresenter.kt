package net.yupol.transmissionremote.app.home

import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.yupol.transmissionremote.app.model.ListResource
import net.yupol.transmissionremote.app.model.Status.ERROR
import net.yupol.transmissionremote.app.model.Status.SUCCESS
import net.yupol.transmissionremote.app.model.mapper.TorrentMapper
import net.yupol.transmissionremote.app.mvp.MvpViewCallback
import net.yupol.transmissionremote.domain.usecase.TorrentListInteractor
import java.util.concurrent.TimeUnit

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
        refresh()
    }

    override fun viewStopped() {
        torrentListDisposable?.dispose()
    }

    fun refresh() {
        view.showLoading()
        startTorrentListLoading()
    }

    fun pauseClicked(torrentId: Int) {
        val d = interactor.pauseTorrent(torrentId)
                .map(torrentMapper::toViewModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ torrent ->
                    view.updateTorrents(torrent)
                }, { error ->
                    view.updateTorrent(torrentId)
                    view.showErrorAlert(error)
                })
    }

    fun resumeClicked(torrentId: Int) {
        val d = interactor.resumeTorrent(torrentId)
                .map(torrentMapper::toViewModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ torrent ->
                    view.updateTorrents(torrent)
                }, { error ->
                    view.updateTorrent(torrentId)
                    view.showErrorAlert(error)
                })
    }

    fun pauseAllClicked() {
        view.showLoading()

        val d = interactor.pauseAllTorrents()
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    refresh()
                }, { error ->
                    view.hideLoading()
                    view.showErrorAlert(error)
                })
    }

    fun resumeAllClicked() {
        view.showLoading()

        val d = interactor.resumeAllTorrents()
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    refresh()
                }, { error ->
                    view.hideLoading()
                    view.showErrorAlert(error)
                })
    }

    private fun startTorrentListLoading() {
        torrentListDisposable?.dispose()

        torrentListDisposable = interactor.loadTorrentList()
                .map(torrentMapper::toViewMode)
                .map {
                    ListResource.success(it)
                }
                .onErrorReturn {
                    ListResource.error(it)
                }
                .repeatWhen { completed ->
                    completed.delay(5, TimeUnit.SECONDS)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    when (result.status) {
                        SUCCESS -> {
                            view.hideLoading()
                            view.showTorrents(result.data!!)
                        }
                        ERROR -> {
                            view.hideLoading()
                            view.showError(result.error!!)
                        }
                        else -> throw IllegalStateException("Unknown status: ${result.status}")
                    }
                }
    }
}
