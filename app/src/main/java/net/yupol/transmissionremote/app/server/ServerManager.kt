package net.yupol.transmissionremote.app.server

import net.yupol.transmissionremote.app.di.ServerComponent
import net.yupol.transmissionremote.domain.model.Server
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ServerManager @Inject constructor(private val componentProvider: Provider<ServerComponent.Builder>) {

    var serverComponent: ServerComponent? = null
        private set

    fun startServerSession(server: Server) {
        serverComponent = componentProvider.get()
                .server(server)
                .build()
    }

    fun finishServerSession() {
        serverComponent = null
    }
}
