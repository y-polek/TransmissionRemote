package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class FileStatEntity(
        @Json(name = "wanted") val wanted: Boolean,
        @Json(name = "priority") val priority: Int,
        @Json(name = "bytesCompleted") val bytesCompleted: Long)
