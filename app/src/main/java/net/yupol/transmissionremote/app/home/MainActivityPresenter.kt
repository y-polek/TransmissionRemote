package net.yupol.transmissionremote.app.home

import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import net.yupol.transmissionremote.app.model.ListResource
import net.yupol.transmissionremote.app.model.Status.*
import net.yupol.transmissionremote.app.model.TorrentViewModel
import net.yupol.transmissionremote.app.model.mapper.TorrentMapper
import net.yupol.transmissionremote.app.mvp.MvpViewCallback
import net.yupol.transmissionremote.app.res.StringResources
import net.yupol.transmissionremote.app.server.ServerManager
import net.yupol.transmissionremote.data.api.NoNetworkException
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.model.Torrent
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

    private var torrents: List<TorrentViewModel>? = null

    private var inSelectionMode: Boolean = false
    private val selectedTorrents = mutableSetOf<Int>()

    override fun viewStarted() {
        serverListSubscription = Observable.combineLatest(
                serverRepo.servers(),
                serverRepo.activeServer(),
                BiFunction { servers: List<Server>, activeServer: Server -> servers to activeServer })
                .subscribe { (servers, activeServer) ->
                    interactor = serverManager.serverComponent?.torrentListInteractor()!!
                    this.activeServer = activeServer

                    view.serverListChanged(servers, activeServer)
                    view.hideFab()

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
                .map { it.toViewModel() }
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
                .map { it.toViewModel() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ torrent ->
                    view.updateTorrents(torrent)
                }, { error ->
                    view.updateTorrent(torrentId)
                    view.showErrorAlert(error)
                })
    }

    fun torrentClicked(torrentId: Int) {
        if (inSelectionMode) {
            if (selectedTorrents.contains(torrentId)) {
                selectedTorrents.remove(torrentId)
            } else {
                selectedTorrents.add(torrentId)
            }
            updateTorrentSelection(torrentId)
            updateSelectionTitle()
            updateSelectionMenu()
        } else {
            view.openTorrentDetails()
        }
    }

    fun torrentLongClicked(torrentId: Int): Boolean {
        if (!inSelectionMode) {
            inSelectionMode = true
            view.startSelection()
            selectedTorrents.add(torrentId)
            updateTorrentSelection(torrentId)
            updateSelectionTitle()
            updateSelectionMenu()
            return true
        }
        return false
    }

    fun selectionModeFinished() {
        inSelectionMode = false
        selectedTorrents.clear()
        updateAllTorrentsSelection()
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

    fun selectAllClicked() {
        if (selectedTorrents.size < torrents?.size ?: 0) {
            selectedTorrents.addAll(torrents?.map { it.id }.orEmpty())
        } else {
            selectedTorrents.clear()
        }
        updateAllTorrentsSelection()
        updateSelectionTitle()
        updateSelectionMenu()
    }

    fun removeSelectedClicked() {

    }

    fun pauseSelectedClicked() {

    }

    fun startNowSelectedClicked() {

    }

    fun renameSelectedClicked() {
        assert(selectedTorrents.size == 1)
    }

    fun setLocationForSelectedClicked() {

    }

    fun verifySelectedClicked() {

    }

    fun reannounceSelectedClicked() {

    }

    //////////////////////////////
    // endregion Public interface
    //////////////////////////////

    private fun startTorrentListLoading() {
        torrentListSubscription?.dispose()

        torrentListSubscription = interactor.loadTorrentList()
                .map { it.toViewModel() }
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
                            torrents = result.data
                            view.showTorrents(torrents!!)
                            view.hideError()
                            view.showFab()
                        }
                        NO_NETWORK -> {
                            torrents = null
                            view.hideTorrents()
                            view.showError(if (result.inAirplaneMode) strRes.networkErrorNoNetworkInAirplaneMode else strRes.networkErrorNoNetwork)
                            view.hideFab()
                        }
                        ERROR -> {
                            torrents = null
                            view.hideTorrents()
                            view.showError(strRes.networkErrorNoConnection, result.error?.message)
                            view.hideFab()
                        }
                        else -> throw IllegalStateException("Unknown status: ${result.status}")
                    }
                }
    }

    private fun updateTorrentSelection(torrentId: Int) {
        val selected = selectedTorrents.contains(torrentId)
        val torrent = torrents?.find { it.id == torrentId }?.copy(selected = selected)
        if (torrent != null) view.updateTorrents(torrent)
    }

    private fun updateAllTorrentsSelection() {
        torrents = torrents?.map { torrent ->
            torrent.copy(selected = selectedTorrents.contains(torrent.id))
        }
        view.showTorrents(torrents ?: return)
    }

    private fun updateSelectionTitle() {
        view.setSelectionTitle(strRes.torrentsCount(selectedTorrents.size))
    }

    private fun updateSelectionMenu() {
        view.setGroupActionsEnabled(selectedTorrents.size > 0)
        view.setRenameActionEnabled(selectedTorrents.size == 1)
    }

    private fun Torrent.toViewModel(): TorrentViewModel {
        return torrentMapper.toViewModel(this, selectedTorrents.contains(id))
    }

    private fun Iterable<Torrent>.toViewModel(): List<TorrentViewModel> {
        return torrentMapper.toViewModel(this, selectedTorrents)
    }
}
