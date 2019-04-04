package net.yupol.transmissionremote.app.opentorrent.presenter

import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter
import net.yupol.transmissionremote.app.opentorrent.model.TorrentFile
import net.yupol.transmissionremote.app.opentorrent.view.OpenTorrentFileView
import net.yupol.transmissionremote.model.Dir
import java.util.*

class OpenTorrentFilePresenter(torrentFilePath: String): MvpNullObjectBasePresenter<OpenTorrentFileView>() {

    val torrentFile = TorrentFile(torrentFilePath)

    private var currentDir: Dir
    private val breadcrumbs: Stack<Dir> = Stack()

    init {
        currentDir = torrentFile.rootDir
        breadcrumbs.push(currentDir)

        if (currentDir.dirs.size == 1 && currentDir.fileIndices.isEmpty()) {
            currentDir = currentDir.dirs.first()
            breadcrumbs.push(currentDir)
        }
    }

    fun viewCreated() {
        view.showDir(currentDir)
        view.showBreadcrumbs(breadcrumbs)
    }

    fun onDirectorySelected(dir: Dir) {
        currentDir = dir
        breadcrumbs.push(dir)
        view.showDir(currentDir)
        view.showBreadcrumbs(breadcrumbs)
    }

    fun onBreadcrumbClicked(position: Int) {
        if (position < 0 || position >= breadcrumbs.size) {
            throw IndexOutOfBoundsException("There is no breadcrumb at position '$position'. " +
                    "# of breadcrumbs: ${breadcrumbs.size}")
        }

        for (i in breadcrumbs.size - 1 downTo position + 1) {
            breadcrumbs.removeAt(i)
        }

        currentDir = breadcrumbs.peek()
        view.showDir(currentDir)
        view.showBreadcrumbs(breadcrumbs)

    }

    fun onSelectAllFilesClicked() {
        torrentFile.selectAllFilesIn(currentDir)
        view.updateFileList()
    }

    fun onSelectNoneFilesClicked() {
        torrentFile.selectNoneFilesIn(currentDir)
        view.updateFileList()
    }
}
