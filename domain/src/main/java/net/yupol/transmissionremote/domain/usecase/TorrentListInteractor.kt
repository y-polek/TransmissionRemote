package net.yupol.transmissionremote.domain.usecase

import io.reactivex.Observable
import net.yupol.transmissionremote.domain.model.Torrent
import javax.inject.Inject

class TorrentListInteractor @Inject constructor(
        private val loadTorrentList: LoadTorrentList)
{
    fun loadTorrentList(): Observable<List<Torrent>> {
        return loadTorrentList.execute()
    }
}
