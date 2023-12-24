package net.yupol.transmissionremote.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics.Event
import com.google.firebase.analytics.FirebaseAnalytics.Param
import javax.inject.Inject

class Analytics @Inject constructor(
    private val analyticsProvider: AnalyticsProvider
) {
    fun logScreenView(screenName: String, screenClass: Class<*>) {
        analyticsProvider.logEvent(
            name = Event.SCREEN_VIEW,
            Param.SCREEN_NAME to screenName,
            Param.SCREEN_CLASS to screenClass.simpleName
        )
    }

    fun setTorrentsCount(count: Int) {
        analyticsProvider.setUserProperty(PROPERTY_TORRENTS_COUNT, TorrentCount.fromCount(count).value)
    }

    fun logStartupTimeSLI(timeMillis: Long) {
        analyticsProvider.logEvent(
            name = EVENT_SLI_STARTUP_TIME,
            PROPERTY_STARTUP_TIME_MILLIS to timeMillis
        )
    }

    companion object {
        private const val PROPERTY_TORRENTS_COUNT = "torrents_count"
        private const val PROPERTY_STARTUP_TIME_MILLIS = "startup_time_millis"

        private const val EVENT_SLI_STARTUP_TIME = "sli_startup_time"
    }
}
