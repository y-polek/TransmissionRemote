package net.yupol.transmissionremote.domain.usecase

import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.Torrent
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import java.util.concurrent.TimeUnit

class PauseResumeTorrent(private val repo: TorrentListRepository) {

    fun pause(torrentId: Int): Single<Torrent> {
        return repo.pauseTorrent(torrentId)
                .andThen(Single.defer {
                    repo.getTorrent(torrentId)
                            .delaySubscription(500, TimeUnit.MILLISECONDS)
                })
    }

    fun resume(torrentId: Int): Single<Torrent> {
        return repo.resumeTorrent(torrentId)
                .andThen(Single.defer {
                    repo.getTorrent(torrentId)
                            .delaySubscription(500, TimeUnit.MILLISECONDS)
                })
    }
}
