package net.yupol.transmissionremote.model

import com.squareup.moshi.Json

data class FreeSpace(@Json(name = "size-bytes") val size: Long)