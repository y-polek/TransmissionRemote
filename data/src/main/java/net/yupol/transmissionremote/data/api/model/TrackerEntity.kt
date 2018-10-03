package net.yupol.transmissionremote.data.api.model

import com.squareup.moshi.Json

class TrackerEntity(
        @Json(name = "id") val id: Int,
        @Json(name = "tier") var tier: Int,
        @Json(name = "announce") var announce: String,
        @Json(name = "scrape") var scrape: String)
