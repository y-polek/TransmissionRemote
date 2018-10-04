package net.yupol.transmissionremote.model.mapper

import net.yupol.transmissionremote.data.api.model.ServerSettingsEntity
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.model.json.ServerSettings

object ServerMapper {

    @JvmStatic
    fun toDomain(server: net.yupol.transmissionremote.model.Server): Server {
        return Server(
                server.name,
                server.host,
                server.port,
                server.useHttps(),
                server.userName,
                server.password,
                server.urlPath,
                server.trustSelfSignedSslCert)
    }

    @JvmStatic
    fun toViewModel(settings: ServerSettingsEntity) = ServerSettings.fromEntity(settings)
}
