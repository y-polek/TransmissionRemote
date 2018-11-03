package net.yupol.transmissionremote.app.di

import android.app.Application
import com.serjltt.moshi.adapters.FirstElement
import com.serjltt.moshi.adapters.Wrapped
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import net.yupol.transmissionremote.app.res.StringResources
import net.yupol.transmissionremote.app.res.StringResourcesImpl
import net.yupol.transmissionremote.data.repository.ServerRepositoryImpl
import net.yupol.transmissionremote.domain.repository.ServerRepository
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
    fun provideServerRepository(app: Application): ServerRepository = ServerRepositoryImpl(app)

    @Provides
    @Singleton
    fun provideStringResources(app: Application): StringResources = StringResourcesImpl(app.resources)
}
