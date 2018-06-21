package net.yupol.transmissionremote.app.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import net.yupol.transmissionremote.app.TransmissionRemote
import net.yupol.transmissionremote.app.server.ServersStorage
import net.yupol.transmissionremote.app.server.SharedPrefsServersStorage
import javax.inject.Singleton

@Singleton
@Module(subcomponents = [NetworkComponent::class])
class ApplicationModule(private val application: TransmissionRemote) {

    @Provides
    fun provideApplication(): TransmissionRemote = application

    @Provides
    fun provideContext(): Context = application

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
                "transmission_remote_shared_prefs",
                Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideServersStorage(preferences: SharedPreferences): ServersStorage {
        return SharedPrefsServersStorage(preferences)
    }
}