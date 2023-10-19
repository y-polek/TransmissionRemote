package net.yupol.transmissionremote.app.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.yupol.transmissionremote.app.analytics.AnalyticsProvider
import net.yupol.transmissionremote.app.analytics.FirebaseAnalyticsProvider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    @Singleton
    @Provides
    fun provideCrashlytics() = FirebaseCrashlytics.getInstance()

    @Singleton
    @Provides
    fun provideRemoteConfig() = Firebase.remoteConfig

    @Singleton
    @Provides
    fun provideAnalytics(@ApplicationContext appContext: Context) = FirebaseAnalytics.getInstance(appContext)

    @Singleton
    @Provides
    fun provideAnalyticsProvider(
        firebaseAnalyticsProvider: FirebaseAnalyticsProvider
    ): AnalyticsProvider = firebaseAnalyticsProvider
}
