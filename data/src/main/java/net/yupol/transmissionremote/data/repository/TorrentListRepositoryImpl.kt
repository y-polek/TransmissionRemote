package net.yupol.transmissionremote.data.repository

import io.reactivex.Observable
import io.reactivex.Single
import net.yupol.transmissionremote.data.api.TransmissionRpcApi
import net.yupol.transmissionremote.data.api.mapper.TorrentMapper
import net.yupol.transmissionremote.domain.model.Torrent
import net.yupol.transmissionremote.domain.repository.TorrentListRepository

class TorrentListRepositoryImpl(
        private val api: TransmissionRpcApi,
        private val mapper: TorrentMapper): TorrentListRepository
{
    override fun getTorrents(): Single<List<Torrent>> {
        return api.torrentList()
                .flatMapObservable {
                    Observable.fromIterable(it)
                }
                .map(mapper::toDomain)
                .toList()
    }
}
