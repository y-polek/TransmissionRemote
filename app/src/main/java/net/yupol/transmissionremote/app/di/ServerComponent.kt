package net.yupol.transmissionremote.app.di

import dagger.BindsInstance
import dagger.Subcomponent
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.usecase.TorrentListInteractor

@ServerScope
@Subcomponent(modules = [ServerModule::class])
interface ServerComponent {

    fun activeServer(): Server

    fun torrentListInteractor(): TorrentListInteractor

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun server(server: Server): Builder

        fun build(): ServerComponent
    }
}
