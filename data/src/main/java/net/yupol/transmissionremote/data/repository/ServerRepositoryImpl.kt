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
    private var activeServerSubject = BehaviorSubject.create<Server>()

    override fun activeServer(): Observable<Server> = activeServerSubject.hide()

    override fun setActiveServer(server: Server) {
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

    override fun removeServer(server: Server) {
        servers.remove(server)
        serversSubject.onNext(servers)

        if (activeServerSubject.value == server) {
            if (servers.isNotEmpty()) {
                setActiveServer(servers.first())
            } else {
                val oldSubject = activeServerSubject
                activeServerSubject = BehaviorSubject.create()
                oldSubject.onComplete()
            }
        }
    }
}
