package net.yupol.transmissionremote.app.home

import com.hannesdorfmann.mosby3.mvp.MvpView
import net.yupol.transmissionremote.app.model.TorrentViewModel

interface MainActivityView: MvpView {

    fun showLoading()

    fun hideLoading()

    fun showTorrents(torrents: List<TorrentViewModel>)

    fun showUpdatedTorrents(vararg torrents: TorrentViewModel)

    fun showError(error: Throwable)

    fun showErrorAlert(error: Throwable)
}