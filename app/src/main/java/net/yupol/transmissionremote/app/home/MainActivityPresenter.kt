package net.yupol.transmissionremote.app.home

import android.text.SpannableStringBuilder
import androidx.core.text.color
import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers.io
import net.yupol.transmissionremote.app.R
import net.yupol.transmissionremote.app.home.filter.Filter
import net.yupol.transmissionremote.app.model.*
import net.yupol.transmissionremote.app.model.Status.*
import net.yupol.transmissionremote.app.model.mapper.TorrentMapper
import net.yupol.transmissionremote.app.mvp.MvpViewCallback
import net.yupol.transmissionremote.app.preferences.Preferences
import net.yupol.transmissionremote.app.res.ColorResources
import net.yupol.transmissionremote.app.res.StringResources
import net.yupol.transmissionremote.app.server.ServerManager
import net.yupol.transmissionremote.data.api.NoNetworkException
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.model.Torrent
import net.yupol.transmissionremote.domain.repository.ServerListRepository
import net.yupol.transmissionremote.domain.usecase.server.ServerInteractor
import net.yupol.transmissionremote.domain.usecase.torrent.TorrentListInteractor
import net.yupol.transmissionremote.utils.deleteIf
import net.yupol.transmissionremote.utils.isNullOrEmpty
import net.yupol.transmissionremote.utils.toArray
import java.io.File
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(
        private val serverManager: ServerManager,
        private val serverListRepo: ServerListRepository,
        private val torrentMapper: TorrentMapper,
        private val strRes: StringResources,
        private val colorRes: ColorResources,
        private val preferences: Preferences): MvpNullObjectBasePresenter<MainActivityView>(), MvpViewCallback
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
    private var filteredTorrents: List<TorrentViewModel>? = null

    private var inSelectionMode: Boolean = false
    private val selectedTorrents = mutableSetOf<Int>()

    private var filter: Filter? = null

    private var searchQuery: String = ""

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
        startTorrentListPolling()
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
                    showErrorAlert(error)
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
                    showErrorAlert(error)
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
        val ids = filteredTorrents?.map(TorrentViewModel::id)?.toArray()
        if (ids.isNullOrEmpty()) return

        view.showLoading()

        requests += torrentInteractor.pauseTorrents(*ids!!)
                .delay(500, MILLISECONDS)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({
                    refreshTorrentList()
                }, { error ->
                    view.hideLoading()
                    showErrorAlert(error)
                })
    }

    fun resumeAllClicked() {
        val ids = filteredTorrents?.map(TorrentViewModel::id)?.toArray()
        if (ids.isNullOrEmpty()) return

        view.showLoading()

        requests += torrentInteractor.resumeTorrents(*ids!!)
                .delay(500, MILLISECONDS)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({
                    refreshTorrentList()
                }, { error ->
                    view.hideLoading()
                    showErrorAlert(error)
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
        if (selectedTorrents.size < filteredTorrents?.size ?: 0) {
            selectedTorrents.addAll(filteredTorrents?.map { it.id }.orEmpty())
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
                .subscribeBy(
                        onSuccess = { torrent ->
                            view.updateTorrents(*torrent.toTypedArray())
                        },
                        onError = ::showErrorAlert
                )
        view.finishSelection()
    }

    fun resumeSelectedClicked() {
        if (selectedTorrents.isEmpty()) return

        val ids = selectedTorrents.toArray()
        requests += torrentInteractor.resumeTorrents(*ids)
                .map { it.toViewModel() }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy(
                        onSuccess = { torrents ->
                            view.updateTorrents(*torrents.toTypedArray())
                        },
                        onError = ::showErrorAlert
                )

        view.finishSelection()
    }

    fun startNowSelectedClicked() {
        if (selectedTorrents.isEmpty()) return

        val ids = selectedTorrents.toArray()
        requests += torrentInteractor.resumeTorrents(*ids, noQueue = true)
                .map { it.toViewModel() }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy(
                        onSuccess = { torrents ->
                            view.updateTorrents(*torrents.toTypedArray())
                        },
                        onError = ::showErrorAlert
                )

        view.finishSelection()
    }

    fun renameSelectedClicked() {
        assert(selectedTorrents.size == 1)

        val torrent = torrents?.findWithId(selectedTorrents.first()) ?: throw IllegalStateException("Trying to rename unknown torrent")
        view.openRenameTorrentDialog(torrent)
    }

    fun renameTorrent(torrent: TorrentViewModel, newName: String) {
        requests += torrentInteractor.renameTorrent(id = torrent.id, oldName = torrent.name.toString(), newName = newName)
                .map { torrentMapper.toViewModel(it, false) }
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy(
                        onSuccess = { view.updateTorrents(it) },
                        onError = ::showErrorAlert)

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
                        onError = ::showErrorAlert)

        view.finishSelection()
    }

    fun reannounceSelectedClicked() {
        if (selectedTorrents.isEmpty()) return

        requests += torrentInteractor.reannounceTorrents(*selectedTorrents.toArray())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy(onError = ::showErrorAlert)

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
                            showErrorAlert(error)
                            view.setTurtleModeEnabled(turtleModeEnabled)
                        })
    }

    fun searchSubmitted(query: String) {
        searchQuery = query

        applyFiltersAndShowTorrents()

        if (inSelectionMode) cleanupSelection()
    }

    fun filterSelected(filter: Filter) {
        this.filter = filter

        view.showActiveFilter(filter)

        applyFiltersAndShowTorrents()
    }

    fun addTorrentByFileSelected() {
        if (view.isStoragePermissionGranted()) {
            view.openTorrentFileChooser(preferences.lastUsedTorrentFileDirectory)
        } else {
            val shouldShowRationale = view.shouldShowStoragePermissionRationale()
            val deniedBefore = preferences.storagePermissionDeniedBefore
            val neverAskAgainSelected = !shouldShowRationale && deniedBefore
            if (neverAskAgainSelected) {
                view.openStoragePermissionRationale()
            } else {
                view.requestStoragePermission(REQUEST_CODE_ADD_TORRENT_BY_FILE)
            }
        }
    }

    fun permissionGranted(requestCode: Int) {
        when (requestCode) {
            REQUEST_CODE_ADD_TORRENT_BY_FILE -> view.openTorrentFileChooser(preferences.lastUsedTorrentFileDirectory)
        }
    }

    fun permissionDenied(requestCode: Int) {
        when (requestCode) {
            REQUEST_CODE_ADD_TORRENT_BY_FILE -> preferences.storagePermissionDeniedBefore = true
        }
    }

    fun torrentFileChosen(file: File) {
        preferences.lastUsedTorrentFileDirectory = file.parent
        view.openAddTorrentDialog(file)
    }

    //////////////////////////////
    // endregion Public interface
    //////////////////////////////

    private fun startTorrentListPolling() {
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
                            applyFiltersAndShowTorrents()
                            view.showLoadingSpeed(
                                    downloadSpeed = torrents!!.totalDownloadSpeed(),
                                    uploadSpeed = torrents!!.totalUploadSpeed())
                            view.showTorrentCount(torrents!!.count())
                            view.hideError()
                            view.showFab()
                        }
                        NO_NETWORK -> {
                            torrents = null
                            filteredTorrents = null
                            view.hideTorrents()
                            view.showError(if (result.inAirplaneMode) strRes.networkErrorNoNetworkInAirplaneMode else strRes.networkErrorNoNetwork)
                            view.hideFab()
                        }
                        ERROR -> {
                            torrents = null
                            filteredTorrents = null
                            view.hideTorrents()
                            view.showError(strRes.networkErrorNoConnection, result.error?.message)
                            view.hideFab()
                        }
                        else -> throw IllegalStateException("Unknown status: ${result.status}")
                    }

                    if (inSelectionMode) cleanupSelection()
                }
    }

    private fun applyFiltersAndShowTorrents() {
        filteredTorrents = torrents?.filteredAndHighlighted(filter)
        if (filteredTorrents != null) {
            view.showTorrents(filteredTorrents!!)
            if (filteredTorrents!!.isEmpty()) {
                showEmptyMessage()
            } else {
                view.hideEmptyMessage()
            }
        }
    }

    private fun showEmptyMessage() {
        val msgId = when {
            searchQuery.isNotBlank() -> R.string.filter_empty_name
            filter != null -> filter!!.emptyMsg
            else -> R.string.filter_empty_all

        }
        view.showEmptyMessage(msgId)
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
        applyFiltersAndShowTorrents()
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
                            showErrorAlert(error)
                        })

        view.finishSelection()
    }

    private fun cleanupSelection() {
        if (filteredTorrents.isNullOrEmpty()) {
            selectedTorrents.clear()
        } else {
            val sortedIds = filteredTorrents!!.map { it.id }.sorted()
            selectedTorrents.deleteIf { sortedIds.binarySearch(it) < 0 }
        }
        updateAllTorrentsSelection()
        updateSelectionTitle()
        updateSelectionMenu()
    }

    private fun showErrorAlert(error: Throwable) {
        val message = error.message ?: strRes.unknownError
        view.showErrorAlert(message)
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

    private fun List<TorrentViewModel>.filteredAndHighlighted(filter: Filter?): List<TorrentViewModel> {
        var sequence = this.asSequence()
        if (searchQuery.isNotBlank()) {
            sequence = sequence.filter { torrent -> torrent.name.contains(searchQuery, ignoreCase = true) }
        }
        if (filter != null) {
            sequence = sequence.filter(filter.apply)
        }
        return sequence.map { torrent ->
            val name = torrent.name.toString()
            val matchStartIdx = name.indexOf(searchQuery, ignoreCase = true)
            if (matchStartIdx < 0) return@map torrent
            val matchEndIdx = matchStartIdx + searchQuery.length

            val nameWithHighlight = SpannableStringBuilder()
                    .append(name.substring(0, matchStartIdx))
                    .color(colorRes.accent) {
                        append(name.substring(matchStartIdx, matchEndIdx))
                    }
                    .append(name.substring(matchEndIdx))

            return@map torrent.copy(name = nameWithHighlight)
        }.toList()
    }

    companion object {
        private const val REQUEST_CODE_ADD_TORRENT_BY_FILE = 3001
    }
}
