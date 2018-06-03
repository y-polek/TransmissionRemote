package net.yupol.transmissionremote.app.di

import dagger.Subcomponent
import net.yupol.transmissionremote.app.home.TorrentListFragment2
import net.yupol.transmissionremote.transport.TransmissionRpcApi
import net.yupol.transmissionremote.transport.di.NetworkModule
import net.yupol.transmissionremote.transport.di.ServerScope

@Subcomponent(modules = [
    NetworkModule::class
])
@ServerScope
interface NetworkComponent {

    fun api(): TransmissionRpcApi

    fun inject(activity: TorrentListFragment2)

    @Subcomponent.Builder
    interface Builder {

        fun networkModule(networkModule: NetworkModule): Builder

        fun build(): NetworkComponent
    }
}