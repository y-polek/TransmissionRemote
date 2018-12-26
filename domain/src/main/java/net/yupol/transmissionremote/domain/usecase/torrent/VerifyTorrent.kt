package net.yupol.transmissionremote.domain.usecase.torrent

import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.Torrent
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VerifyTorrent @Inject constructor(private val repo: TorrentListRepository) {

    fun execute(vararg ids: Int): Single<List<Torrent>> {
        return repo.verifyTorrents(*ids)
                .andThen(Single.defer {
                    repo.getTorrents(*ids)
                            .delaySubscription(500, TimeUnit.MILLISECONDS)
                })
    }
}
