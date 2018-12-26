package net.yupol.transmissionremote.domain.usecase.torrent

import io.reactivex.Completable
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import javax.inject.Inject

class ReannounceTorrent @Inject constructor(private val repo: TorrentListRepository) {

    fun execute(vararg ids: Int): Completable {
        return repo.reannounceTorrents(*ids)
    }
}
