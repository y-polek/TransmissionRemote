package net.yupol.transmissionremote.app.home.filter

import androidx.annotation.StringRes
import net.yupol.transmissionremote.domain.model.Torrent

data class Filter(@StringRes val name: Int, val apply: (Torrent) -> Boolean)
