package net.yupol.transmissionremote.domain.usecase.torrent

import io.reactivex.Observable
import net.yupol.transmissionremote.domain.model.Torrent
import net.yupol.transmissionremote.domain.repository.TorrentListRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoadTorrentList @Inject constructor(private val repo: TorrentListRepository, private val updateInterval: Int) {

    fun execute(): Observable<List<Torrent>> {
        return Observable.interval(0, updateInterval.toLong(), TimeUnit.SECONDS)
                .switchMap {
                    repo.getAllTorrents().toObservable()
                }
    }
}
