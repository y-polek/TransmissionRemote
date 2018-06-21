package net.yupol.transmissionremote.app.server

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import net.yupol.transmissionremote.model.Server
import javax.inject.Inject

class ServersRepository @Inject constructor(private val storage: ServersStorage) {

    private val servers = MutableLiveData<List<Server>>()
    private val activeServer = MutableLiveData<Server>()

    init {
        servers.value = storage.readServers()
    }

    fun getServers(): LiveData<List<Server>> = servers

    fun addServer(server: Server) {
        val newServers = (servers.value ?: listOf()) + server
        servers.value = newServers
        if (activeServer.value == null) {
            activeServer.value = server
        }
        persistServers()
    }

    fun removeServer(server: Server) {
        val newServers = (servers.value ?: listOf()) - server
        if (server == activeServer.value) {
            setActiveServer(newServers.firstOrNull())
        }
        servers.value = newServers
        persistServers()
    }

    fun getActiveServer(): LiveData<Server> = activeServer

    fun setActiveServer(server: Server?) {
        activeServer.value = server
        storage.writeActiveServer(server)
    }

    fun persistServers() {
        storage.writeServers(servers.value!!)
    }

    fun getServerById(id: String): Server? {
        return servers.value?.firstOrNull { it.id == id }
    }

    fun updateServer(server: Server) {
        val currentServers = servers.value ?: throw IllegalStateException("Repo not initialized")

        val idx = currentServers.indexOfFirst { it.id == server.id }
        if (idx < 0) throw IllegalStateException("Server $server not found in repository")

        servers.value = currentServers.toMutableList().apply {
            this[idx] = server
        }

        persistServers()

        if (activeServer.value?.id == server.id) {
            activeServer.value = server
            storage.writeActiveServer(server)
        }
    }
}
