package net.yupol.transmissionremote.domain.usecase

import io.reactivex.Observable
import io.reactivex.Single
import net.yupol.transmissionremote.domain.model.Torrent
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class RenameTorrent @Inject constructor(private val repo: TorrentListRepository) {

    fun execute(id: Int, oldName: String, newName: String): Single<Torrent> {
        return repo.renameTorrent(id = id, oldName = oldName, newName = newName)
                .andThen(Single.defer {
                    repo.getTorrents(id)
                            .flatMapObservable { Observable.fromIterable(it) }
                            .firstOrError()
                            .delaySubscription(500, MILLISECONDS)
                })
    }
}
