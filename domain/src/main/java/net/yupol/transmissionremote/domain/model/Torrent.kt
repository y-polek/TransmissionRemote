package net.yupol.transmissionremote.domain.model

import net.yupol.transmissionremote.domain.model.Torrent.Status.*

data class Torrent(
        val id: Int,
        val name: String,
        val addedDate: Long,
        val totalSize: Long,
        val percentDone: Double,
        val status: Status,
        val downloadRate: Long,
        val uploadRate: Long,
        val eta: Long,
        val uploadedSize: Long,
        val uploadRatio: Double,
        val errorId: Int?,
        val errorString: String?,
        val isFinished: Boolean,
        val sizeWhenDone: Long,
        val leftUntilDone: Long,
        val peersGettingFromUs: Int,
        val peersSendingToUs: Int,
        val webseedsSendingToUs: Int,
        val queuePosition: Int,
        val recheckProgress: Double,
        val doneDate: Long)
{
    enum class Status {
        UNKNOWN,
        STOPPED,
        CHECK_WAIT,
        CHECK,
        DOWNLOAD_WAIT,
        DOWNLOAD,
        SEED_WAIT,
        SEED
    }

    val isChecking = status == CHECK

    val isActive = peersGettingFromUs > 0 || peersSendingToUs > 0 || webseedsSendingToUs > 0 || isChecking

    val isDownloading = status == DOWNLOAD || status == DOWNLOAD_WAIT

    val isCompleted = leftUntilDone <= 0 && sizeWhenDone > 0

    val isSeeding = status == SEED || status == SEED_WAIT

    val isPaused = status == STOPPED
}
