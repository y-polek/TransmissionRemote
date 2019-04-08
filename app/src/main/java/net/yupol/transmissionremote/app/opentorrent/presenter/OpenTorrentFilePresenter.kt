package net.yupol.transmissionremote.app.opentorrent.presenter

import android.util.Log
import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers.io
import net.yupol.transmissionremote.app.model.PriorityViewModel.HIGH
import net.yupol.transmissionremote.app.model.PriorityViewModel.LOW
import net.yupol.transmissionremote.app.opentorrent.model.TorrentFile
import net.yupol.transmissionremote.app.opentorrent.view.OpenTorrentFileView
import net.yupol.transmissionremote.app.res.StringResources
import net.yupol.transmissionremote.app.utils.TextUtils
import net.yupol.transmissionremote.domain.repository.ServerRepository
import net.yupol.transmissionremote.domain.usecase.torrent.AddTorrentFile
import net.yupol.transmissionremote.model.Dir
import java.io.File
import java.util.*

class OpenTorrentFilePresenter @AssistedInject constructor(
        @Assisted private val torrentFilePath: String,
        private val addTorrentFile: AddTorrentFile,
        private val serverRepository: ServerRepository,
        private val strRes: StringResources): MvpNullObjectBasePresenter<OpenTorrentFileView>()
{

    val torrentFile = TorrentFile(torrentFilePath)

    private var currentDir: Dir
    private val breadcrumbs: Stack<Dir> = Stack()

    private var downloadDirectory: String = ""
    private var trashTorrentFile: Boolean = false
    private var startTorrentWhenAdded: Boolean = true

    private var addTorrentRequest: Disposable? = null

    init {
        currentDir = torrentFile.rootDir
        breadcrumbs.push(currentDir)

        if (currentDir.dirs.size == 1 && currentDir.fileIndices.isEmpty()) {
            currentDir = currentDir.dirs.first()
            breadcrumbs.push(currentDir)
        }

        serverRepository.defaultDownloadDir()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe(object : SingleObserver<String> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(dir: String) {
                        if (downloadDirectory.isEmpty()) {
                            downloadDirectory = dir
                            view.setDownloadDirectory(downloadDirectory)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("Presenter", "Error", e)
                    }

                })
    }

    fun viewCreated() {
        view.showNameText(torrentFile.name)
        view.showDir(currentDir)
        view.showBreadcrumbs(breadcrumbs)
        view.setDownloadDirectory(downloadDirectory)
        view.setTrashTorrentFile(trashTorrentFile)
        view.setStartTorrentWhenAdded(startTorrentWhenAdded)
        calculateAndDisplaySizeSummary()
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
        calculateAndDisplaySizeSummary()
    }

    fun onSelectNoneFilesClicked() {
        torrentFile.selectNoneFilesIn(currentDir)
        view.updateFileList()
        calculateAndDisplaySizeSummary()
    }

    fun onFileSelectionChanged() {
        calculateAndDisplaySizeSummary()
    }

    fun onAddButtonClicked() {

        val paused = !startTorrentWhenAdded
        val filesUnwanted = mutableListOf<Int>()
        val priorityHigh = mutableListOf<Int>()
        val priorityLow = mutableListOf<Int>()
        torrentFile.files.forEachIndexed { index, file ->
            if (!file.wanted) filesUnwanted.add(index)
            when (file.priority) {
                HIGH -> priorityHigh.add(index)
                LOW -> priorityLow.add(index)
                else -> {}
            }
        }

        addTorrentRequest = addTorrentFile.execute(
                file = File(torrentFilePath),
                destinationDir = downloadDirectory,
                paused = paused,
                filesUnwanted = filesUnwanted,
                priorityHigh = priorityHigh,
                priorityLow = priorityLow)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeBy(
                        onSuccess = { result ->
                            Log.d("Add torrent", "result: $result")
                        },
                        onError = { error ->
                            Log.e("Add torrent", "Error: ${error.message}", error)
                        }
                )
    }

    fun onDownloadLocationTextChanged(text: String) {
        if (text == downloadDirectory) return

        downloadDirectory = text
    }

    fun onTrashTorrentFileChanged(trashTorrentFile: Boolean) {
        this.trashTorrentFile = trashTorrentFile
    }

    fun onStartTorrentWhenAddedChanged(startTorrentWhenAdded: Boolean) {
        this.startTorrentWhenAdded = startTorrentWhenAdded
    }

    private fun calculateAndDisplaySizeSummary() {
        val selectedSize = torrentFile.files.asSequence()
                .filter { it.wanted }
                .map { it.length }
                .sum()
        val text = strRes.torrentFileSizeSummary(
                torrentFile.files.size,
                TextUtils.displayableSize(torrentFile.size),
                TextUtils.displayableSize(selectedSize))
        view.showSizeText(text)
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(torrentFilePath: String): OpenTorrentFilePresenter
    }
}
