package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class FileEntity(
        @Json(name = "name") val path: String,
        @Json(name = "length") val length: Long)
