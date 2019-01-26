package net.yupol.transmissionremote.app.home

import androidx.annotation.StringRes
import com.hannesdorfmann.mosby3.mvp.MvpView
import net.yupol.transmissionremote.app.home.filter.Filter
import net.yupol.transmissionremote.app.model.TorrentViewModel
import net.yupol.transmissionremote.domain.model.Server

interface MainActivityView: MvpView {

    fun showWelcomeScreen()

    fun hideWelcomeScreen()

    fun serverListChanged(servers: List<Server>, activeServer: Server)

    fun showLoading()

    fun hideLoading()

    fun showTorrents(torrents: List<TorrentViewModel>)

    fun showLoadingSpeed(downloadSpeed: Long, uploadSpeed: Long)

    fun hideTorrents()

    fun showFab()

    fun hideFab()

    fun updateTorrents(vararg torrents: TorrentViewModel)

    fun updateTorrent(torrentId: Int)

    fun showError(summary: String, details: String? = null)

    fun hideError()

    fun showErrorAlert(error: Throwable)

    fun showEmptyMessage(@StringRes msgId: Int)

    fun hideEmptyMessage()

    fun startSelection()

    fun finishSelection()

    fun setSelectionTitle(title: String)

    fun setGroupActionsEnabled(enabled: Boolean)

    fun setRenameActionEnabled(enabled: Boolean)

    fun setTurtleModeEnabled(enabled: Boolean)

    fun showActiveFilter(filter: Filter)

    fun showTorrentCount(counts: Map<Filter, Int>)

    // region Routing
    fun openAddServerScreen()

    fun openServerSettings(server: Server)

    fun openNetworkSettings()

    fun openTorrentDetails()

    fun openRemoveTorrentOptionsDialog()

    fun openDeleteTorrentDataConfirmation()

    fun openRenameTorrentDialog(torrent: TorrentViewModel)
    // endregion
}
