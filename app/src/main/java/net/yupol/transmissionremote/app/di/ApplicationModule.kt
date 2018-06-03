package net.yupol.transmissionremote.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import net.yupol.transmissionremote.app.TransmissionRemote
import javax.inject.Singleton

@Singleton
@Module(subcomponents = [NetworkComponent::class])
class ApplicationModule(private val application: TransmissionRemote) {

    @Provides
    @Singleton
    fun application(): TransmissionRemote = application

    @Provides
    @Singleton
    fun context(): Context = application
}