package net.yupol.transmissionremote.mockserver.model

abstract class Torrent {
    data class Dates(
        val added: Long,
        val created: Long,
        val done: Long = 0,
        val activity: Long = added
    )
    data class Size(
        val totalSize: Long,
        val sizeWhenDone: Long = totalSize,
        val leftUntilDone: Long = sizeWhenDone,
        val desiredAvailable: Long = totalSize,
        val haveUnchecked: Long = 0,
        val haveValid: Long = 0,
        val downloadedEver: Long = 0,
        val uploadedEver: Long = 0,
        val pieceSize: Long = totalSize,
        val pieceCount: Long = 1
    )
}
