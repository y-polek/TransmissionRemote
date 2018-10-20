package net.yupol.transmissionremote.data.api.mapper

import net.yupol.transmissionremote.data.api.model.TorrentEntity
import net.yupol.transmissionremote.domain.model.Torrent
import kotlin.reflect.jvm.internal.impl.javax.inject.Inject

class TorrentMapper @Inject constructor() {

    fun toDomain(torrent: TorrentEntity): Torrent {
        return Torrent(
                id = torrent.id,
                name = torrent.name,
                addedDate = torrent.addedDate,
                totalSize = torrent.totalSize,
                percentDone = torrent.percentDone,
                status = torrent.status,
                downloadRate = torrent.downloadRate,
                uploadRate = torrent.uploadRate,
                eta = torrent.eta,
                uploadedSize = torrent.uploadedSize,
                uploadRatio = torrent.uploadRatio,
                errorId = torrent.errorId,
                errorString = torrent.errorString,
                isFinished = torrent.isFinished,
                sizeWhenDone = torrent.sizeWhenDone,
                leftUntilDone = torrent.leftUntilDone,
                peersGettingFromUs = torrent.peersGettingFromUs,
                peersSendingToUs = torrent.peersSendingToUs,
                webseedsSendingToUs = torrent.webseedsSendingToUs,
                queuePosition = torrent.queuePosition,
                recheckProgress = torrent.recheckProgress,
                doneDate = torrent.doneDate)
    }
}
