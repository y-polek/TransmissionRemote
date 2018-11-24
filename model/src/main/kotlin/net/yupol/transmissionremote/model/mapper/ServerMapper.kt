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

    @Deprecated(message = "For migration only")
    @JvmStatic
    fun toModel(server: Server): net.yupol.transmissionremote.model.Server {
        val model = net.yupol.transmissionremote.model.Server(server.name, server.host, server.port ?: 0, server.login!!, server.password!!)
        model.rpcUrl = server.rpcPath
        model.useHttps = false
        model.trustSelfSignedSslCert = false
        model.lastUpdateDate = 0
        return model
    }

    @Deprecated(message = "For migration only")
    @JvmStatic
    fun toModel(servers: List<Server>): List<net.yupol.transmissionremote.model.Server> {
        return servers.map(this::toModel)
    }

    @JvmStatic
    fun toViewModel(settings: ServerSettingsEntity): ServerSettings = ServerSettings.fromEntity(settings)
}
