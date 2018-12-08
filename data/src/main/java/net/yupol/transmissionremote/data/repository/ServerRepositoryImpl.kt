package net.yupol.transmissionremote.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.repository.ServerRepository

class ServerRepositoryImpl(private val prefs: SharedPreferences, private val moshi: Moshi): ServerRepository {

    companion object {
        private const val KEY_SERVER_LIST = "key_server_list"
        private const val KEY_ACTIVE_SERVER = "key_active_server"
    }

    private val servers: MutableList<Server> = mutableListOf()
    private val serversSubject = BehaviorSubject.createDefault(emptyList<Server>())
    private var activeServerSubject = BehaviorSubject.createDefault(Server.EMPTY)

    init {
        restoreServers()
        restoreActiveServer()
    }

    override fun activeServer(): Observable<Server> = activeServerSubject.hide()

    override fun setActiveServer(server: Server) {
        if (activeServerSubject.value == server) return

        if (serversSubject.value.contains(server)) {
            activeServerSubject.onNext(server)
            persistActiveServer()
        } else {
            throw IllegalStateException("Unknown server: $server")
        }
    }

    override fun servers(): Observable<List<Server>> = serversSubject.hide()

    override fun addServer(server: Server) {
        servers.add(server)
        persistServers()
        serversSubject.onNext(servers)
    }

    override fun removeServer(withName: String) {
        servers.removeAll { withName == it.name }
        persistServers()
        serversSubject.onNext(servers)

        if (activeServerSubject.value.name == withName) {
            if (servers.isNotEmpty()) {
                setActiveServer(servers.first())
            } else {
                setActiveServer(Server.EMPTY)
            }
        }
    }

    override fun findServer(withName: String) = servers.first { withName == it.name }

    override fun updateServer(withName: String, server: Server) {
        val idx = servers.indexOfFirst { withName == it.name }
        if (idx >= 0) {
            servers[idx] = server
            serversSubject.onNext(servers)
            persistServers()

            if (activeServerSubject.value.name == withName) {
                setActiveServer(server)
            }
        }
    }

    private fun persistServers() {
        val type = Types.newParameterizedType(List::class.java, Server::class.java)
        val adapter: JsonAdapter<List<Server>> = moshi.adapter(type)
        val serversInJson = adapter.toJson(servers)
        prefs.edit {
            putString(KEY_SERVER_LIST, serversInJson)
        }
    }

    private fun persistActiveServer() {
        val adapter = moshi.adapter(Server::class.java)
        val serverInJson = adapter.toJson(activeServerSubject.value)
        prefs.edit {
            putString(KEY_ACTIVE_SERVER, serverInJson)
        }
    }

    private fun restoreServers() {
        val serversInJson = prefs.getString(KEY_SERVER_LIST, null)
        if (serversInJson.isNullOrEmpty()) return

        val type = Types.newParameterizedType(List::class.java, Server::class.java)
        val adapter: JsonAdapter<List<Server>> = moshi.adapter(type)
        val servers = adapter.fromJson(serversInJson) ?: return
        this.servers.clear()
        this.servers.addAll(servers)
        serversSubject.onNext(this.servers)
    }

    private fun restoreActiveServer() {
        val serverInJson = prefs.getString(KEY_ACTIVE_SERVER, null)
        if (serverInJson.isNullOrEmpty()) return

        val adapter = moshi.adapter(Server::class.java)
        val server = adapter.fromJson(serverInJson) ?: return
        setActiveServer(server)
    }
}
