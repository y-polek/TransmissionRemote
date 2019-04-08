package net.yupol.transmissionremote.app.di

import dagger.BindsInstance
import dagger.Subcomponent
import net.yupol.transmissionremote.app.opentorrent.view.OpenTorrentFileActivity
import net.yupol.transmissionremote.domain.model.Server
import net.yupol.transmissionremote.domain.usecase.server.ServerInteractor
import net.yupol.transmissionremote.domain.usecase.torrent.TorrentListInteractor

@ServerScope
@Subcomponent(modules = [ServerModule::class])
interface ServerComponent {

    fun activeServer(): Server

    fun torrentListInteractor(): TorrentListInteractor

    fun serverInteractor(): ServerInteractor

    fun inject(activity: OpenTorrentFileActivity)

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun server(server: Server): Builder

        fun build(): ServerComponent
    }
}
