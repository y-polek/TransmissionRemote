package net.yupol.transmissionremote.domain.usecase.torrent

import io.reactivex.Completable
import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.Torrent
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PauseResumeTorrent @Inject constructor(private val repo: TorrentListRepository) {

    fun pause(vararg ids: Int): Single<List<Torrent>> {
        return repo.pauseTorrents(*ids)
                .andThen(Single.defer {
                    repo.getTorrents(*ids)
                            .delaySubscription(500, TimeUnit.MILLISECONDS)
                })
    }

    fun resume(vararg ids: Int, noQueue: Boolean): Single<List<Torrent>> {
        return repo.resumeTorrents(*ids, noQueue = noQueue)
                .andThen(Single.defer {
                    repo.getTorrents(*ids)
                            .delaySubscription(500, TimeUnit.MILLISECONDS)
                })
    }

    fun pauseAll(): Completable {
        return repo.pauseAll()
    }

    fun resumeAll(): Completable {
        return repo.resumeAll()
    }
}
