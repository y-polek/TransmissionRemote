package net.yupol.transmissionremote.domain.usecase

import io.reactivex.Completable
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import javax.inject.Inject

class RemoveTorrent @Inject constructor(private val repo: TorrentListRepository) {

    fun removeTorrents(vararg ids: Int, deleteData: Boolean): Completable {
        return repo.removeTorrents(*ids, deleteData = deleteData)
    }
}
