package net.yupol.transmissionremote.app.di

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.serjltt.moshi.adapters.FirstElement
import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import net.yupol.transmissionremote.app.res.StringResources
import net.yupol.transmissionremote.app.res.StringResourcesImpl
import net.yupol.transmissionremote.data.repository.ServerListRepositoryImpl
import net.yupol.transmissionremote.domain.repository.ServerListRepository
import javax.inject.Singleton

@Module(subcomponents = [ServerComponent::class])
class AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(Wrapped.ADAPTER_FACTORY)
                .add(FirstElement.ADAPTER_FACTORY)
                .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)

    }

    @Provides
    @Singleton
    fun provideServerRepository(prefs: SharedPreferences, moshi: Moshi): ServerListRepository = ServerListRepositoryImpl(prefs, moshi)

    @Provides
    @Singleton
    fun provideStringResources(app: Application): StringResources = StringResourcesImpl(app.resources)
}
