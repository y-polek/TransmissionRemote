package net.yupol.transmissionremote.model.mapper

import net.yupol.transmissionremote.data.api.model.PeerEntity
import net.yupol.transmissionremote.model.json.Peer

object PeerMapper {

    @JvmStatic
    fun toViewModel(entity: PeerEntity) = Peer.fromEntity(entity)

    @JvmStatic
    fun Array<PeerEntity>.toViewModel(): Array<Peer> {
        return map(PeerMapper::toViewModel).toTypedArray()
    }
}
