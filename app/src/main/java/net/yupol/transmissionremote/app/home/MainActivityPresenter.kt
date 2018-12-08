package net.yupol.transmissionremote.app.home

import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import net.yupol.transmissionremote.app.model.ListResource
import net.yupol.transmissionremote.app.model.Status.*
import net.yupol.transmissionremote.app.model.mapper.TorrentMapper
import net.yupol.transmissionremote.app.mvp.MvpViewCallback
import net.yupol.transmissionremote.app.res.StringResources
import net.yupol.transmissionremote.app.server.ServerManager
import net.yupol.transmissionremote.data.api.NoNetworkException
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.repository.ServerRepository
import net.yupol.transmissionremote.domain.usecase.TorrentListInteractor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
        private val serverManager: ServerManager,
        private val serverRepo: ServerRepository,
        private val torrentMapper: TorrentMapper,
        private val strRes: StringResources): MvpNullObjectBasePresenter<MainActivityView>(), MvpViewCallback
{
    private lateinit var interactor: TorrentListInteractor
    private lateinit var activeServer: Server

    private var torrentListSubscription: Disposable? = null
    private var serverListSubscription: Disposable? = null

    override fun viewStarted() {
        serverListSubscription = Observable.combineLatest(
                serverRepo.servers(),
                serverRepo.activeServer(),
                BiFunction { servers: List<Server>, activeServer: Server -> servers to activeServer })
                .subscribe { (servers, activeServer) ->
                    interactor = serverManager.serverComponent?.torrentListInteractor()!!
                    this.activeServer = activeServer
                    view.serverListChanged(servers, activeServer)
                    if (servers.isEmpty()) {
                        view.showWelcomeScreen()
                    } else {
                        view.hideWelcomeScreen()
                        refreshTorrentList()
                    }
                }
    }

    override fun viewStopped() {
        torrentListSubscription?.dispose()
        serverListSubscription?.dispose()
    }

    ///////////////////////////
    // region Public interface
    ///////////////////////////

    fun addServerClicked() {
        view.openAddServerScreen()
    }

    fun activeServerSelected(server: Server) {
        serverRepo.setActiveServer(server)
    }

    fun refreshTorrentList() {
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
                    refreshTorrentList()
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
                    refreshTorrentList()
                }, { error ->
                    view.hideLoading()
                    view.showErrorAlert(error)
                })
    }

    fun serverSettingsClicked() {
        view.openServerSettings(activeServer)
    }

    fun networkSettingsClicked() {
        view.openNetworkSettings()
    }

    fun retryButtonClicked() {
        refreshTorrentList()
    }

    //////////////////////////////
    // endregion Public interface
    //////////////////////////////

    private fun startTorrentListLoading() {
        torrentListSubscription?.dispose()

        torrentListSubscription = interactor.loadTorrentList()
                .map(torrentMapper::toViewMode)
                .map {
                    ListResource.success(it)
                }
                .onErrorReturn { error ->
                    return@onErrorReturn if (error is NoNetworkException) {
                        ListResource.noNetwork(error.inAirplaneMode)
                    } else {
                        ListResource.error(error)
                    }
                }
                .repeatWhen { completed ->
                    completed.delay(5, TimeUnit.SECONDS)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    view.hideLoading()
                    when (result.status) {
                        SUCCESS -> {
                            view.showTorrents(result.data!!)
                            view.hideError()
                        }
                        NO_NETWORK -> {
                            view.hideTorrents()
                            view.showError(if (result.inAirplaneMode) strRes.networkErrorNoNetworkInAirplaneMode else strRes.networkErrorNoNetwork)
                        }
                        ERROR -> {
                            view.hideTorrents()
                            view.showError(strRes.networkErrorNoConnection, result.error?.message)
                        }
                        else -> throw IllegalStateException("Unknown status: ${result.status}")
                    }
                }
    }
}
