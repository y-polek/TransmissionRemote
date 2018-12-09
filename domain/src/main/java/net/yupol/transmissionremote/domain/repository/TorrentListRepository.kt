package net.yupol.transmissionremote.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.Torrent

interface TorrentListRepository {

    fun getAllTorrents(): Single<List<Torrent>>

    fun getTorrents(vararg ids: Int): Single<List<Torrent>>

    fun pauseTorrents(vararg ids: Int): Completable

    fun resumeTorrents(vararg ids: Int, noQueue: Boolean = false): Completable

    fun pauseAll(): Completable

    fun resumeAll(): Completable
}
