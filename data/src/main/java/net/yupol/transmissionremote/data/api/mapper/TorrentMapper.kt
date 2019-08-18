package net.yupol.transmissionremote.data.api.mapper

import net.yupol.transmissionremote.data.api.model.TorrentEntity
import net.yupol.transmissionremote.domain.model.Torrent
import net.yupol.transmissionremote.domain.model.Torrent.Status.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TorrentMapper @Inject constructor() {

    fun toDomain(torrent: TorrentEntity): Torrent {
        val status = when (torrent.status) {
            0 -> STOPPED
            1 -> CHECK_WAIT
            2 -> CHECK
            3 -> DOWNLOAD_WAIT
            4 -> DOWNLOAD
            5 -> SEED_WAIT
            6 -> SEED
            else -> UNKNOWN
        }

        return Torrent(
                id = torrent.id,
                name = torrent.name,
                addedDate = torrent.addedDate,
                totalSize = torrent.totalSize,
                percentDone = torrent.percentDone,
                status = status,
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
                doneDate = torrent.doneDate,
                downloadDir = torrent.downloadDir)
    }

    fun toDomain(torrents: List<TorrentEntity>) = torrents.map { toDomain(it) }
}
