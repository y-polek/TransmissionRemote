package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class PeerEntity(
        @Json(name = "address")  val address: String,
        @Json(name = "clientName")  val clientName: String,
        @Json(name = "clientIsChoked")  val clientIsChoked: Boolean = false,
        @Json(name = "clientIsInterested")  val clientIsInterested: Boolean = false,
        @Json(name = "flagStr")  val flagStr: String,
        @Json(name = "isDownloadingFrom")  val isDownloadingFrom: Boolean = false,
        @Json(name = "isEncrypted")  val isEncrypted: Boolean = false,
        @Json(name = "isIncoming")  val isIncoming: Boolean = false,
        @Json(name = "isUploadingTo")  val isUploadingTo: Boolean = false,
        @Json(name = "isUTP")  val isUTP: Boolean = false,
        @Json(name = "peerIsChoked")  val peerIsChoked: Boolean = false,
        @Json(name = "peerIsInterested")  val peerIsInterested: Boolean = false,
        @Json(name = "port")  val port: Int,
        @Json(name = "progress")  val progress: Double,
        @Json(name = "rateToClient")  val rateToClient: Long,
        @Json(name = "rateToPeer")  val rateToPeer: Long)