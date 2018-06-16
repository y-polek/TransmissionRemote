package net.yupol.transmissionremote.app.di

import android.content.Context
import dagger.Component
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.server.ServersStorage
import javax.inject.Singleton

@Component(modules = [
    ApplicationModule::class
])
@Singleton
interface ApplicationComponent {

    fun application(): TransmissionRemote

    fun context(): Context

    fun serversStorage(): ServersStorage

    fun networkComponentBuilder(): NetworkComponent.Builder
}
