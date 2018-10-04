package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

class TrackerEntity(
        @JvmField @Json(name = "id") val id: Int,
        @JvmField @Json(name = "tier") var tier: Int,
        @JvmField @Json(name = "announce") var announce: String,
        @JvmField @Json(name = "scrape") var scrape: String)
