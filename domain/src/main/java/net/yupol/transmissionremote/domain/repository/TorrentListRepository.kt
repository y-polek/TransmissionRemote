package net.yupol.transmissionremote.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.Torrent

interface TorrentListRepository {

    fun getTorrents(): Single<List<Torrent>>

    fun getTorrent(id: Int): Single<Torrent>

    fun pauseTorrent(id: Int): Completable

    fun resumeTorrent(id: Int): Completable
}
