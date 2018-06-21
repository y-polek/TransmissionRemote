package net.yupol.transmissionremote.app.di

import android.content.Context
import dagger.Component
import net.yupol.transmissionremote.app.MainActivity
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.home.HomeActivity
import net.yupol.transmissionremote.app.opentorrent.DownloadLocationDialogFragment
import net.yupol.transmissionremote.app.server.ServersRepository
import net.yupol.transmissionremote.app.server.ServersStorage
import javax.inject.Singleton

@Component(modules = [
    ApplicationModule::class
])
@Singleton
interface ApplicationComponent {

    fun application(): TransmissionRemote

    fun context(): Context

    fun serversRepository(): ServersRepository

    fun networkComponentBuilder(): NetworkComponent.Builder

    fun inject(app: TransmissionRemote)

    fun inject(activity: HomeActivity)

    fun inject(activity: MainActivity)

    fun inject(fragment: DownloadLocationDialogFragment)
}
