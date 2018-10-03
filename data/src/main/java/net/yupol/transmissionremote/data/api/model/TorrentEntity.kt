package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class TorrentEntity(
        @Json(name = "id") val id: Int,
        @Json(name = "name") val name: String,
        @Json(name = "addedDate") val addedDate: Long,
        @Json(name = "totalSize") val totalSize: Long,
        @Json(name = "percentDone") val percentDone: Double,
        @Json(name = "status") val status: Int,
        @Json(name = "rateDownload") val downloadRate: Int,
        @Json(name = "rateUpload") val uploadRate: Int,
        @Json(name = "eta") val eta: Long,
        @Json(name = "uploadedEver") val uploadedSize: Long,
        @Json(name = "uploadRatio") val uploadRatio: Double,
        @Json(name = "error") val errorId: Int,
        @Json(name = "errorString") val errorString: String,
        @Json(name = "isFinished") val isFinished: Boolean,
        @Json(name = "sizeWhenDone") val sizeWhenDone: Long,
        @Json(name = "leftUntilDone") val leftUntilDone: Long,
        @Json(name = "peersGettingFromUs") val peersGettingFromUs: Int,
        @Json(name = "peersSendingToUs") val peersSendingToUs: Int,
        @Json(name = "webseedsSendingToUs") val webseedsSendingToUs: Int,
        @Json(name = "queuePosition") val queuePosition: Int,
        @Json(name = "recheckProgress") val recheckProgress: Double,
        @Json(name = "doneDate") val doneDate: Long)