package net.yupol.transmissionremote.app.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.drawer.HeaderView
import net.yupol.transmissionremote.app.home.MainActivity
import net.yupol.transmissionremote.app.opentorrent.DownloadLocationDialogFragment
import net.yupol.transmissionremote.app.preferences.ServerListActivity
import net.yupol.transmissionremote.app.server.AddServerActivity
import net.yupol.transmissionremote.app.server.ServerDetailsFragment
import net.yupol.transmissionremote.app.torrentdetails.TorrentDetailsActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(app: TransmissionRemote)

    fun inject(activity: MainActivity)

    fun inject(activity: TorrentDetailsActivity)

    fun inject(activity: ServerListActivity)

    fun inject(view: HeaderView)

    fun inject(fragment: DownloadLocationDialogFragment)

    fun inject(fragment: ServerDetailsFragment)

    fun inject(activity: AddServerActivity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AppComponent
    }
}
