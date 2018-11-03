package net.yupol.transmissionremote.model.mapper

import net.yupol.transmissionremote.data.api.model.ServerSettingsEntity
import net.yupol.transmissionremote.domain.model.ProtectedProperty
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.model.json.ServerSettings
import java.util.*

object ServerMapper {

    @JvmStatic
    fun toDomain(server: net.yupol.transmissionremote.model.Server): Server {
        return Server(
                server.name,
                server.host.protected(),
                server.port,
                server.useHttps(),
                server.userName.protected(),
                server.password.protected(),
                server.urlPath,
                server.trustSelfSignedSslCert)
    }

    @Deprecated(message = "For migration only")
    @JvmStatic
    fun toModel(server: Server): net.yupol.transmissionremote.model.Server {
        val model = net.yupol.transmissionremote.model.Server(server.name, server.host.value, server.port ?: 0, server.login.value!!, server.password.value!!)
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

    private fun <T> T.protected() = ProtectedProperty(this)
}
