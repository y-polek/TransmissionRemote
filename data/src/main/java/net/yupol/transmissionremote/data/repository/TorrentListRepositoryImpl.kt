package net.yupol.transmissionremote.data.repository

import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.Torrent
import net.yupol.transmissionremote.domain.repository.TorrentListRepository

class TorrentListRepositoryImpl: TorrentListRepository {

    override fun getTorrents(): Single<List<Torrent>> {
        TODO("not implemented")
    }
}
