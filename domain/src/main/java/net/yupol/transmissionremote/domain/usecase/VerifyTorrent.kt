package net.yupol.transmissionremote.domain.usecase

import io.reactivex.Completable
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import javax.inject.Inject

class VerifyTorrent @Inject constructor(private val repo: TorrentListRepository) {

    fun execute(vararg ids: Int): Completable {
        return repo.verifyTorrents(*ids)
    }
}
