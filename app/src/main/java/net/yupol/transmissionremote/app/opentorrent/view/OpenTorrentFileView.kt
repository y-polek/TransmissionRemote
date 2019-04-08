package net.yupol.transmissionremote.app.opentorrent.view

import com.hannesdorfmann.mosby3.mvp.MvpView
import net.yupol.transmissionremote.model.Dir
import java.util.*

interface OpenTorrentFileView: MvpView {

    fun showNameText(text: String)

    fun showSizeText(text: String)

    fun showDir(dir: Dir)

    fun showBreadcrumbs(path: Stack<Dir>)

    fun updateFileList()

    fun setDownloadDirectory(text: String)

    fun setTrashTorrentFile(trash: Boolean)

    fun setStartTorrentWhenAdded(start: Boolean)
}
