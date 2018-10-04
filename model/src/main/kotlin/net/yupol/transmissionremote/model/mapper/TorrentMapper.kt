package net.yupol.transmissionremote.model.mapper

import net.yupol.transmissionremote.data.api.model.TorrentEntity
import net.yupol.transmissionremote.data.api.model.TorrentInfoEntity
import net.yupol.transmissionremote.model.json.Torrent
import net.yupol.transmissionremote.model.json.TorrentInfo

object TorrentMapper {

    @JvmStatic
    fun toViewModel(torrent: TorrentEntity): Torrent {
        return Torrent.fromEntity(torrent)
    }

    @JvmStatic
    fun List<TorrentEntity>.toViewModel(): List<Torrent> {
        return map(TorrentMapper::toViewModel).toList()
    }

    @JvmStatic
    fun toViewModel(entity: TorrentInfoEntity) = TorrentInfo.fromEntity(entity)
}