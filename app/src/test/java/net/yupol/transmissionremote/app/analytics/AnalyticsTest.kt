package net.yupol.transmissionremote.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics.Event
import com.google.firebase.analytics.FirebaseAnalytics.Param
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AnalyticsTest {

    private val analyticsProvider = mock<AnalyticsProvider>()
    private val analytics = Analytics(analyticsProvider)

    @Test
    fun `test logScreenView`() {
        // given
        class MainScreen

        // when
        analytics.logScreenView("Main", MainScreen::class.java)

        // then
        verify(analyticsProvider).logEvent(
            Event.SCREEN_VIEW,
            Param.SCREEN_NAME to "Main",
            Param.SCREEN_CLASS to "MainScreen"
        )
    }

    @Test
    fun `test setTorrentsCount`() {
        // when
        analytics.setTorrentsCount(42)

        // then
        verify(analyticsProvider).setUserProperty("torrents_count", "41-50")
    }

    @Test
    fun `test logStartupTimeSLI`() {
        // when
        analytics.logStartupTimeSLI(345L)

        // then
        verify(analyticsProvider).logEvent(
            name = "sli_startup_time",
            "startup_time_millis" to 345L
        )
    }
}
