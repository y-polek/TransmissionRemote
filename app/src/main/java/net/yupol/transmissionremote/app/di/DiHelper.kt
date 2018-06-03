package net.yupol.transmissionremote.app.di

import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.model.Server
import net.yupol.transmissionremote.transport.di.NetworkModule

class DiHelper(private val application: TransmissionRemote) {

    private val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(application))
                .build()
    }

    var networkComponent: NetworkComponent? = null
        private set

    fun initNetworkComponent(server: Server) {
        networkComponent = applicationComponent.networkComponentBuilder()
                .networkModule(NetworkModule(server))
                .build()
    }

    fun clearNetworkComponent() {
        networkComponent = null
    }
}