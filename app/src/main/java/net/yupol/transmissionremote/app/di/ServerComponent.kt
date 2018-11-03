package net.yupol.transmissionremote.app.di

import dagger.BindsInstance
import dagger.Subcomponent
import net.yupol.transmissionremote.app.home.MainActivityPresenter
import net.yupol.transmissionremote.domain.model.Server

@ServerScope
@Subcomponent(modules = [ServerModule::class])
interface ServerComponent {

    fun activeServer(): Server

    fun mainActivityPresenter(): MainActivityPresenter

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun server(server: Server): Builder

        fun build(): ServerComponent
    }
}
