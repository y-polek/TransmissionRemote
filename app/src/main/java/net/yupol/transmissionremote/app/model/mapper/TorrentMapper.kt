package net.yupol.transmissionremote.app.model.mapper

import net.yupol.transmissionremote.app.model.TorrentViewModel
import net.yupol.transmissionremote.data.model.ErrorType
import net.yupol.transmissionremote.domain.model.Torrent
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class TorrentMapper @Inject constructor() {

    fun toViewModel(torrent: Torrent): TorrentViewModel {
        return TorrentViewModel(
                id = torrent.id,
                name = torrent.name,
                downloadedSize = torrent.sizeWhenDone - torrent.leftUntilDone,
                totalSize = torrent.totalSize,
                sizeWhenDone = torrent.sizeWhenDone,
                completed = torrent.leftUntilDone <= 0 && torrent.sizeWhenDone > 0,
                uploadedSize = torrent.uploadedSize,
                uploadRatio = max(torrent.uploadRatio, 0.0),
                downloadRate = torrent.downloadRate,
                uploadRate = torrent.uploadRate,
                paused = torrent.status == 0,
                rechecking = torrent.recheckProgress > 0.0,
                progressPercent = torrent.percentDone,
                recheckProgressPercent = torrent.recheckProgress,
                eta = torrent.eta,
                errorMessage = torrent.errorString,
                errorType = ErrorType.fromCode(torrent.errorId)
        )
    }

    fun toViewMode(torrents: Iterable<Torrent>) = torrents.map(this::toViewModel)
}
