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

    fun removeTorrents(vararg ids: Int, deleteData: Boolean): Completable

    fun verifyTorrents(vararg ids: Int): Completable

    fun reannounceTorrents(vararg ids: Int): Completable

    fun renameTorrent(id: Int, oldName: String, newName: String): Completable
}
