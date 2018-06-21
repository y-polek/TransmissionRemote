package net.yupol.transmissionremote.app.server

import net.yupol.transmissionremote.model.Server

class FakeServersStorage(vararg initServers: Server): ServersStorage {

    private val servers = mutableListOf<Server>()
    private var activeServer: Server? = null

    init {
        servers.addAll(initServers)
    }

    override fun readServers(): List<Server> = servers

    override fun writeServers(servers: List<Server>) {
        this.servers.clear()
        this.servers.addAll(servers.map { Server.fromJson(it.toJson()) })
    }

    override fun readActiveServer() = activeServer

    override fun writeActiveServer(server: Server?) {
        activeServer = server
    }
}