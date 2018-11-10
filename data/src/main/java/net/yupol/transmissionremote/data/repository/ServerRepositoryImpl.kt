package net.yupol.transmissionremote.data.repository

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.repository.ServerRepository

class ServerRepositoryImpl(application: Application): ServerRepository {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    private val servers: MutableList<Server> = mutableListOf()
    private val serversSubject = BehaviorSubject.createDefault(emptyList<Server>())
    private var activeServerSubject = BehaviorSubject.createDefault(Server.EMPTY)
    override fun activeServer(): Observable<Server> = activeServerSubject.hide()

    override fun setActiveServer(server: Server) {
        if (activeServerSubject.value == server) return

        if (serversSubject.value.contains(server)) {
            activeServerSubject.onNext(server)
        } else {
            throw IllegalStateException("Unknown server: $server")
        }
    }

    override fun servers(): Observable<List<Server>> = serversSubject.hide()

    override fun addServer(server: Server) {
        servers.add(server)
        serversSubject.onNext(servers)
    }

    override fun removeServer(withName: String) {
        servers.removeAll { withName == it.name }
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
        if (idx > 0) {
            servers[idx] = server
            serversSubject.onNext(servers)

            if (activeServerSubject.value.name == withName) {
                setActiveServer(server)
            }
        }
    }
}
