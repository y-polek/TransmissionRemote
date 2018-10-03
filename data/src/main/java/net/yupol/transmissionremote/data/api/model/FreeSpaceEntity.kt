package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

data class FreeSpaceEntity(@Json(name = "size-bytes") val size: Long)