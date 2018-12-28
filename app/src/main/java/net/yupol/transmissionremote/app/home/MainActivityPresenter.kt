package net.yupol.transmissionremote.app.home

import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers.io
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
import net.yupol.transmissionremote.domain.repository.ServerListRepository
import net.yupol.transmissionremote.domain.usecase.server.ServerInteractor
import net.yupol.transmissionremote.domain.usecase.torrent.TorrentListInteractor
import net.yupol.transmissionremote.utils.deleteIf
import net.yupol.transmissionremote.utils.toArray
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
        private val serverManager: ServerManager,
        private val serverListRepo: ServerListRepository,
        private val torrentMapper: TorrentMapper,
        private val strRes: StringResources): MvpNullObjectBasePresenter<MainActivityView>(), MvpViewCallback
{
    private lateinit var torrentInteractor: TorrentListInteractor
    private lateinit var serverInteractor: ServerInteractor
    private lateinit var activeServer: Server

    private var torrentListSubscription: Disposable? = null
    private var serverListSubscription: Disposable? = null
    private var turtleModeSubscription: Disposable? = null
    private val requests = CompositeDisposable()
    private val turtleModeRequests = CompositeDisposable()

    private var torrents: List<TorrentViewModel>? = null

    private var inSelectionMode: Boolean = false
    private val selectedTorrents = mutableSetOf<Int>()

    var turtleModeEnabled: Boolean = false
        private set

    override fun viewStarted() {
        serverListSubscription = Observable.combineLatest(
                serverListRepo.servers(),
                serverListRepo.activeServer(),
                BiFunction { servers: List<Server>, activeServer: Server -> servers to activeServer })
                .subscribe { (servers, activeServer) ->
                    torrentInteractor = serverManager.serverComponent?.torrentListInteractor()!!
                    serverInteractor = serverManager.serverComponent?.serverInteractor()!!
                    this.activeServer = activeServer

                    view.serverListChanged(servers, activeServer)
                    view.hideFab()

                    if (servers.isEmpty()) {
                        view.showWelcomeScreen()
                    } else {
                        view.hideWelcomeScreen()
                        refreshTorrentList()
                        startTurtleModePolling()
                    }
                }
    }

    override fun viewStopped() {
        torrentListSubscription?.dispose()
        serverListSubscription?.dispose()
        turtleModeSubscription?.dispose()
        requests.clear()
        turtleModeRequests.clear()
    }

    ///////////////////////////
    // region Public interface
    ///////////////////////////

    fun addServerClicked() {
        view.openAddServerScreen()
    }

    fun activeServerSelected(server: Server) {
        serverListRepo.setActiveServer(server)
    }

    fun refreshTorrentList() {
        view.showLoading()
        startTorrentListLoading()
    }

    fun pauseClicked(torrentId: Int) {
        requests += torrentInteractor.pauseTorrents(torrentId)
                .map { it.toViewModel() }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({ torrent ->
                    view.updateTorrents(*torrent.toTypedArray())
                }, { error ->
                    view.updateTorrent(torrentId)
                    view.showErrorAlert(error)
                })
    }

    fun resumeClicked(torrentId: Int) {
        requests += torrentInteractor.resumeTorrents(torrentId)
                .map { it.toViewModel() }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({ torrent ->
                    view.updateTorrents(*torrent.toTypedArray())
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

    fun selectionModeRestored() {
        updateSelectionTitle()
        updateSelectionMenu()
    }

    fun selectionModeFinished() {
        inSelectionMode = false
        selectedTorrents.clear()
        updateAllTorrentsSelection()
    }

    fun pauseAllClicked() {
        view.showLoading()

        requests += torrentInteractor.pauseAllTorrents()
                .delay(500, MILLISECONDS)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({
                    refreshTorrentList()
                }, { error ->
                    view.hideLoading()
                    view.showErrorAlert(error)
                })
    }

    fun resumeAllClicked() {
        view.showLoading()

        requests += torrentInteractor.resumeAllTorrents()
                .delay(500, MILLISECONDS)
                .subscribeOn(io())
                .observeOn(mainThread())
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
        view.openRemoveTorrentOptionsDialog()
    }

    fun removeSelectedTorrentsFromListClicked() {
        removeSelectedTorrents(deleteData = false)
    }

    fun removeSelectedTorrentsFromListAndDeleteDataClicked() {
        view.openDeleteTorrentDataConfirmation()
    }

    fun deleteSelectedTorrentsDataConfirmed() {
        removeSelectedTorrents(deleteData = true)
    }

    fun pauseSelectedClicked() {
        if (selectedTorrents.isEmpty()) return

        val ids = selectedTorrents.toArray()
        requests += torrentInteractor.pauseTorrents(*ids)
                .map { it.toViewModel() }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({ torrent ->
                    view.updateTorrents(*torrent.toTypedArray())
                }, { error ->
                    view.showErrorAlert(error)
                })

        view.finishSelection()
    }

    fun resumeSelectedClicked() {
        if (selectedTorrents.isEmpty()) return

        val ids = selectedTorrents.toArray()
        requests += torrentInteractor.resumeTorrents(*ids)
                .map { it.toViewModel() }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({ torrents ->
                    view.updateTorrents(*torrents.toTypedArray())
                }, { error ->
                    view.showErrorAlert(error)
                })

        view.finishSelection()
    }

    fun startNowSelectedClicked() {
        if (selectedTorrents.isEmpty()) return

        val ids = selectedTorrents.toArray()
        requests += torrentInteractor.resumeTorrents(*ids, noQueue = true)
                .map { it.toViewModel() }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({ torrents ->
                    view.updateTorrents(*torrents.toTypedArray())
                }, { error ->
                    view.showErrorAlert(error)
                })

        view.finishSelection()
    }

    fun renameSelectedClicked() {
        assert(selectedTorrents.size == 1)

        val torrent = torrents?.findWithId(selectedTorrents.first()) ?: throw IllegalStateException("Trying to rename unknown torrent")
        view.openRenameTorrentDialog(torrent)
    }

    fun renameTorrent(torrent: TorrentViewModel, newName: String) {
        requests += torrentInteractor.renameTorrent(id = torrent.id, oldName = torrent.name, newName = newName)
                .map { torrentMapper.toViewModel(it, false) }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy(
                        onSuccess = { view.updateTorrents(it) },
                        onError = view::showErrorAlert)

        view.finishSelection()
    }

    fun setLocationForSelectedClicked() {

    }

    fun verifySelectedClicked() {
        if (selectedTorrents.isEmpty()) return

        requests += torrentInteractor.verifyLocalData(*selectedTorrents.toArray())
                .map { it.toViewModel() }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy(
                        onSuccess = { torrents ->
                            view.updateTorrents(*torrents.toTypedArray())
                        },
                        onError = view::showErrorAlert)

        view.finishSelection()
    }

    fun reannounceSelectedClicked() {
        if (selectedTorrents.isEmpty()) return

        requests += torrentInteractor.reannounceTorrents(*selectedTorrents.toArray())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy(onError = view::showErrorAlert)

        view.finishSelection()
    }

    fun turtleModeToggled() {
        turtleModeRequests.clear()

        turtleModeRequests += serverInteractor.setTurtleModeEnabled(!turtleModeEnabled)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy(
                        onSuccess = { enabled ->
                            turtleModeEnabled = enabled
                            view.setTurtleModeEnabled(enabled)
                        },
                        onError = { error ->
                            view.showErrorAlert(error)
                            view.setTurtleModeEnabled(turtleModeEnabled)
                        })
    }

    //////////////////////////////
    // endregion Public interface
    //////////////////////////////

    private fun startTorrentListLoading() {
        torrentListSubscription?.dispose()

        torrentListSubscription = torrentInteractor.loadTorrentList()
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
                    completed.delay(5, SECONDS)
                }
                .subscribeOn(io())
                .observeOn(mainThread())
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

                    if (inSelectionMode) cleanupSelection()
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

    private fun removeSelectedTorrents(deleteData: Boolean) {
        view.showLoading()

        requests += torrentInteractor.removeTorrents(*selectedTorrents.toIntArray(), deleteData = deleteData)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy(
                        onComplete = {
                            refreshTorrentList()
                        },
                        onError = { error ->
                            view.hideLoading()
                            view.showErrorAlert(error)
                        })

        view.finishSelection()
    }

    private fun cleanupSelection() {
        if (torrents.isNullOrEmpty()) {
            selectedTorrents.clear()
        } else {
            val sortedIds = torrents!!.map { it.id }.sorted()
            selectedTorrents.deleteIf { sortedIds.binarySearch(it) < 0 }
        }
        updateAllTorrentsSelection()
        updateSelectionTitle()
        updateSelectionMenu()
    }

    private fun Iterable<Torrent>.toViewModel(): List<TorrentViewModel> {
        return torrentMapper.toViewModel(this, selectedTorrents)
    }

    private fun List<TorrentViewModel>.findWithId(id: Int) = find { it.id == id }

    private fun startTurtleModePolling() {
        turtleModeSubscription?.dispose()

        turtleModeSubscription = serverInteractor.isTurtleModeEnabled()
                .retryWhen { error ->
                    error.delay(5, SECONDS)
                }
                .repeatWhen { completed ->
                    completed.delay(5, SECONDS)
                }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy { enabled ->
                    turtleModeEnabled = enabled
                    view.setTurtleModeEnabled(enabled)
                }
    }
}
