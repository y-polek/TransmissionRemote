package net.yupol.transmissionremote.app.server

import android.content.SharedPreferences
import net.yupol.transmissionremote.model.Server

class SharedPrefsServersStorage(private val prefs: SharedPreferences): ServersStorage {

    companion object {
        private const val KEY_SERVERS = "key_servers"
        private const val KEY_ACTIVE_SERVER = "key_active_server"
    }

    override fun writeServers(servers: List<Server>) {
        prefs.edit()
                .putStringSet(KEY_SERVERS, servers.map { it.toJson() }.toSet())
                .apply()
    }

    override fun readServers(): List<Server> {
        return prefs.getStringSet(KEY_SERVERS, emptySet<String>())
                .mapNotNull { Server.fromJson(it) }
    }

    override fun writeActiveServer(server: Server?) {
        val serverInJson = server?.toJson()
        prefs.edit()
                .putString(KEY_ACTIVE_SERVER, serverInJson)
                .apply()
    }

    override fun readActiveServer(): Server? {
        return prefs.getString(KEY_ACTIVE_SERVER, null)?.let { Server.fromJson(it) }
    }
}