package net.yupol.transmissionremote.data.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import net.yupol.transmissionremote.data.api.TransmissionRpcApi
import net.yupol.transmissionremote.data.api.mapper.TorrentMapper
import net.yupol.transmissionremote.data.api.rpc.RpcArgs
import net.yupol.transmissionremote.domain.model.Torrent
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import javax.inject.Inject

class TorrentListRepositoryImpl @Inject constructor(
        private val api: TransmissionRpcApi,
        private val mapper: TorrentMapper): TorrentListRepository
{
    override fun getAllTorrents(): Single<List<Torrent>> {
        return api.torrentList()
                .flatMapObservable {
                    Observable.fromIterable(it)
                }
                .map(mapper::toDomain)
                .toList()
    }

    override fun getTorrents(vararg ids: Int): Single<List<Torrent>> {
        return api.torrentList(*ids)
                .map(mapper::toDomain)
    }

    override fun pauseTorrents(vararg ids: Int): Completable {
        return api.stopTorrents(*ids)
    }

    override fun resumeTorrents(vararg ids: Int, noQueue: Boolean): Completable {
        return if (noQueue) api.startTorrentsNoQueue(*ids) else api.startTorrents(*ids)
    }

    override fun pauseAll(): Completable {
        return api.stopTorrents()
    }

    override fun resumeAll(): Completable {
        return api.startTorrents()
    }

    override fun removeTorrents(vararg ids: Int, deleteData: Boolean): Completable {
        if (ids.isEmpty()) throw IllegalArgumentException("List of IDs cannot be empty, otherwise all torrents will be removed")
        return if (deleteData) api.removeTorrentsAndDeleteData(*ids) else api.removeTorrents(*ids)
    }

    override fun verifyTorrents(vararg ids: Int): Completable {
        if (ids.isEmpty()) throw IllegalArgumentException("List of IDs cannot be empty, otherwise all torrents will be verified")
        return api.verifyTorrents(*ids)
    }

    override fun reannounceTorrents(vararg ids: Int): Completable {
        if (ids.isEmpty()) throw IllegalArgumentException("List of IDs cannot be empty, otherwise all torrents will be reannounced")
        return api.reannounceTorrents(*ids)
    }

    override fun renameTorrent(id: Int, oldName: String, newName: String): Completable {
        return api.renameTorrent(RpcArgs.renameTorrent(id = id, path = oldName, name = newName))
    }
}
