package net.yupol.transmissionremote.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics.Event
import com.google.firebase.analytics.FirebaseAnalytics.Param

class Analytics(
    private val analyticsProvider: AnalyticsProvider
) {
    fun logScreenView(screenName: String, screenClass: Class<*>) {
        analyticsProvider.logEvent(
            Event.SCREEN_VIEW,
            Param.SCREEN_NAME to screenName,
            Param.SCREEN_CLASS to screenClass.simpleName
        )
    }

    fun setTorrentsCount(count: Int) {
        analyticsProvider.setUserProperty(PROPERTY_TORRENTS_COUNT, TorrentCount.fromCount(count).value)
    }

    companion object {
        private const val PROPERTY_TORRENTS_COUNT = "torrents_count"
    }
}
