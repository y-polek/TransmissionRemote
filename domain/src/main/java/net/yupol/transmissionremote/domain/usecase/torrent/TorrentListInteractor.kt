package net.yupol.transmissionremote.domain.usecase.torrent

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.AddTorrentResult
import net.yupol.transmissionremote.domain.model.Torrent
import java.io.File
import javax.inject.Inject

class TorrentListInteractor @Inject constructor(
        private val loadTorrentList: LoadTorrentList,
        private val pauseResumeTorrent: PauseResumeTorrent,
        private val removeTorrent: RemoveTorrent,
        private val verifyTorrent: VerifyTorrent,
        private val reannounceTorrent: ReannounceTorrent,
        private val renameTorrent: RenameTorrent,
        private val addTorrentFile: AddTorrentFile)
{
    fun loadTorrentList(): Observable<List<Torrent>> {
        return loadTorrentList.execute()
    }

    fun pauseTorrents(vararg ids: Int): Single<List<Torrent>> {
        return pauseResumeTorrent.pause(*ids)
    }

    fun resumeTorrents(vararg ids: Int, noQueue: Boolean = false): Single<List<Torrent>> {
        return pauseResumeTorrent.resume(*ids, noQueue = noQueue)
    }

    fun pauseAllTorrents(): Completable {
        return pauseResumeTorrent.pauseAll()
    }

    fun resumeAllTorrents(): Completable {
        return pauseResumeTorrent.resumeAll()
    }

    fun removeTorrents(vararg ids: Int, deleteData: Boolean = false): Completable {
        return removeTorrent.removeTorrents(*ids, deleteData = deleteData)
    }

    fun verifyLocalData(vararg ids: Int): Single<List<Torrent>> {
        return verifyTorrent.execute(*ids)
    }

    fun reannounceTorrents(vararg ids: Int): Completable {
        return reannounceTorrent.execute(*ids)
    }

    fun renameTorrent(id: Int, oldName: String, newName: String): Single<Torrent> {
        return renameTorrent.execute(id = id, oldName = oldName, newName = newName)
    }

    fun addTorrentFile(file: File, destinationDir: String, paused: Boolean): Single<AddTorrentResult> {
        return addTorrentFile.execute(file, destinationDir, paused)
    }
}
