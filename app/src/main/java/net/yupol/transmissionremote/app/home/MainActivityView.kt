package net.yupol.transmissionremote.app.home

import com.hannesdorfmann.mosby3.mvp.MvpView
import net.yupol.transmissionremote.app.model.TorrentViewModel
import net.yupol.transmissionremote.domain.model.Server

interface MainActivityView: MvpView {

    fun showWelcomeScreen()

    fun hideWelcomeScreen()

    fun serverListChanged(servers: List<Server>, activeServer: Server)

    fun showLoading()

    fun hideLoading()

    fun showTorrents(torrents: List<TorrentViewModel>)

    fun hideTorrents()

    fun showFab()

    fun hideFab()

    fun updateTorrents(vararg torrents: TorrentViewModel)

    fun updateTorrent(torrentId: Int)

    fun showError(summary: String, details: String? = null)

    fun hideError()

    fun showErrorAlert(error: Throwable)

    fun startSelection()

    fun finishSelection()

    fun setSelectionTitle(title: String)

    // region Routing

    fun openAddServerScreen()

    fun openServerSettings(server: Server)

    fun openNetworkSettings()

    fun openTorrentDetails()

    // endregion
}
