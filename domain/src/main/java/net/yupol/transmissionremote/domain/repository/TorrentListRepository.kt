package net.yupol.transmissionremote.domain.repository

import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.Torrent

interface TorrentListRepository {

    fun getTorrents(): Single<List<Torrent>>
}
