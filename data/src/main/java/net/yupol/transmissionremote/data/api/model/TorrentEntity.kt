package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class TorrentEntity(
        @JvmField @Json(name = "id") val id: Int,
        @JvmField @Json(name = "name") val name: String,
        @JvmField @Json(name = "addedDate") val addedDate: Long,
        @JvmField @Json(name = "totalSize") val totalSize: Long,
        @JvmField @Json(name = "percentDone") val percentDone: Double,
        @JvmField @Json(name = "status") val status: Int,
        @JvmField @Json(name = "rateDownload") val downloadRate: Int,
        @JvmField @Json(name = "rateUpload") val uploadRate: Int,
        @JvmField @Json(name = "eta") val eta: Long,
        @JvmField @Json(name = "uploadedEver") val uploadedSize: Long,
        @JvmField @Json(name = "uploadRatio") val uploadRatio: Double,
        @JvmField @Json(name = "error") val errorId: Int,
        @JvmField @Json(name = "errorString") val errorString: String,
        @JvmField @Json(name = "isFinished") val isFinished: Boolean,
        @JvmField @Json(name = "sizeWhenDone") val sizeWhenDone: Long,
        @JvmField @Json(name = "leftUntilDone") val leftUntilDone: Long,
        @JvmField @Json(name = "peersGettingFromUs") val peersGettingFromUs: Int,
        @JvmField @Json(name = "peersSendingToUs") val peersSendingToUs: Int,
        @JvmField @Json(name = "webseedsSendingToUs") val webseedsSendingToUs: Int,
        @JvmField @Json(name = "queuePosition") val queuePosition: Int,
        @JvmField @Json(name = "recheckProgress") val recheckProgress: Double,
        @JvmField @Json(name = "doneDate") val doneDate: Long)