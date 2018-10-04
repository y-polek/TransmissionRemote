package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class PeerEntity(
        @JvmField @Json(name = "address")  val address: String,
        @JvmField @Json(name = "clientName")  val clientName: String,
        @JvmField @Json(name = "clientIsChoked")  val clientIsChoked: Boolean = false,
        @JvmField @Json(name = "clientIsInterested")  val clientIsInterested: Boolean = false,
        @JvmField @Json(name = "flagStr")  val flagStr: String,
        @JvmField @Json(name = "isDownloadingFrom")  val isDownloadingFrom: Boolean = false,
        @JvmField @Json(name = "isEncrypted")  val isEncrypted: Boolean = false,
        @JvmField @Json(name = "isIncoming")  val isIncoming: Boolean = false,
        @JvmField @Json(name = "isUploadingTo")  val isUploadingTo: Boolean = false,
        @JvmField @Json(name = "isUTP")  val isUTP: Boolean = false,
        @JvmField @Json(name = "peerIsChoked")  val peerIsChoked: Boolean = false,
        @JvmField @Json(name = "peerIsInterested")  val peerIsInterested: Boolean = false,
        @JvmField @Json(name = "port")  val port: Int,
        @JvmField @Json(name = "progress")  val progress: Double,
        @JvmField @Json(name = "rateToClient")  val rateToClient: Long,
        @JvmField @Json(name = "rateToPeer")  val rateToPeer: Long)