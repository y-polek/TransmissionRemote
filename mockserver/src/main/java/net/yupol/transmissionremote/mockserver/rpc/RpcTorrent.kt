package net.yupol.transmissionremote.mockserver.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RpcTorrent(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("status") val status: Int,
    @SerialName("totalSize") val totalSize: Long,
    @SerialName("sizeWhenDone") val sizeWhenDone: Long,
    @SerialName("leftUntilDone") val leftUntilDone: Long,
    @SerialName("desiredAvailable") val desiredAvailable: Long,
    @SerialName("haveUnchecked") val haveUnchecked: Long,
    @SerialName("haveValid") val haveValid: Long,
    @SerialName("downloadedEver") val downloadedEver: Long,
    @SerialName("uploadedEver") val uploadedEver: Long,
    @SerialName("pieceSize") val pieceSize: Long,
    @SerialName("pieceCount") val pieceCount: Long,
    @SerialName("isFinished") val isFinished: Boolean = false,
    @SerialName("addedDate") val addedDate: Long,
    @SerialName("dateCreated") val dateCreated: Long,
    @SerialName("doneDate") val doneDate: Long,
    @SerialName("activityDate") val activityDate: Long,
    @SerialName("eta") val eta: Long = -1,
    @SerialName("percentDone") val percentDone: Double = 0.0,
    @SerialName("rateDownload") val rateDownload: Long = 0,
    @SerialName("rateUpload") val rateUpload: Long = 0,
    @SerialName("uploadRatio") val uploadRatio: Double = 0.0,
    @SerialName("recheckProgress") val recheckProgress: Double = 0.0,
    @SerialName("secondsDownloading") val secondsDownloading: Int,
    @SerialName("secondsSeeding") val secondsSeeding: Int,
    @SerialName("queuePosition") val queuePosition: Int,
    @SerialName("peersGettingFromUs") val peersGettingFromUs: Int = 0,
    @SerialName("peersSendingToUs") val peersSendingToUs: Int = 0,
    @SerialName("webseedsSendingToUs") val webseedsSendingToUs: Int = 0,
    @SerialName("error") val errorId: Int = 0,
    @SerialName("errorString") val errorString: String = "",
    @SerialName("files") val files: List<RpcFile>,
    @SerialName("fileStats") val fileStats: List<RpcFileStats>,
    @SerialName("peers") val peers: List<RpcPeer> = emptyList(),
    @SerialName("downloadDir") val downloadDir: String,
    @SerialName("creator") val creator: String,
    @SerialName("comment") val comment: String,
    @SerialName("magnetLink") val magnetLink: String,

)
