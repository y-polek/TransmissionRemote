package net.yupol.transmissionremote.app.server

import net.yupol.transmissionremote.model.Server

interface ServersStorage {

    fun readServers(): List<Server>

    fun writeServers(servers: List<Server>)

    fun readActiveServer(): Server?

    fun writeActiveServer(server: Server?)
}