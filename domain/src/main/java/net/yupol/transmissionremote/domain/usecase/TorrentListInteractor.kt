package net.yupol.transmissionremote.domain.usecase

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.Torrent
import javax.inject.Inject

class TorrentListInteractor @Inject constructor(
        private val loadTorrentList: LoadTorrentList,
        private val pauseResumeTorrent: PauseResumeTorrent)
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
}
