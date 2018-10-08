package net.yupol.transmissionremote.data.api.mapper

import net.yupol.transmissionremote.data.api.model.TorrentEntity
import net.yupol.transmissionremote.domain.model.Torrent
import kotlin.reflect.jvm.internal.impl.javax.inject.Inject

class TorrentMapper @Inject constructor() {

    fun toDomain(torrent: TorrentEntity): Torrent {
        return Torrent(torrent.id, torrent.name)
    }
}
