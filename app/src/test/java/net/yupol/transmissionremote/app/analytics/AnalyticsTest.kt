package net.yupol.transmissionremote.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics.Event
import com.google.firebase.analytics.FirebaseAnalytics.Param
import net.yupol.transmissionremote.app.transport.request.AddTorrentRequest
import net.yupol.transmissionremote.app.transport.request.RenameRequest
import net.yupol.transmissionremote.app.transport.request.SessionGetRequest
import net.yupol.transmissionremote.app.transport.request.SetLocationRequest
import net.yupol.transmissionremote.app.transport.request.TorrentGetRequest
import net.yupol.transmissionremote.app.transport.request.VerifyTorrentRequest
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
    fun `test logOkHttRequestStart`() {
        // when
        analytics.logOkHttpRequestStart(SessionGetRequest::class.java)

        // then
        verify(analyticsProvider).logEvent(
            name = "okhttp_request_start",
            "request_class" to "SessionGetRequest"
        )
    }

    @Test
    fun `test logOkHttpRequestSuccess`() {
        // when
        analytics.logOkHttpRequestSuccess(TorrentGetRequest::class.java, 23)

        // then
        verify(analyticsProvider).logEvent(
            name = "okhttp_request_success",
            "request_class" to "TorrentGetRequest",
            "response_time_millis" to "23"
        )
    }

    @Test
    fun `test logOkHttpRequestFailure`() {
        // when
        analytics.logOkHttpRequestFailure(AddTorrentRequest::class.java, 89)

        // then
        verify(analyticsProvider).logEvent(
            name = "okhttp_request_failure",
            "request_class" to "AddTorrentRequest",
            "response_time_millis" to "89"
        )
    }

    @Test
    fun `test logRobospiceRequestStart`() {
        // when
        analytics.logRobospiceRequestStart(RenameRequest::class.java)

        // then
        verify(analyticsProvider).logEvent(
            name = "robospice_request_start",
            "request_class" to "RenameRequest"
        )
    }

    @Test
    fun `test logRobospiceRequestSuccess`() {
        // when
        analytics.logRobospiceRequestSuccess(SetLocationRequest::class.java, 58)

        // then
        verify(analyticsProvider).logEvent(
            name = "robospice_request_success",
            "request_class" to "SetLocationRequest",
            "response_time_millis" to "58"
        )
    }

    @Test
    fun `test logRobospiceRequestFailure`() {
        // when
        analytics.logRobospiceRequestFailure(VerifyTorrentRequest::class.java, 123)

        // then
        verify(analyticsProvider).logEvent(
            name = "robospice_request_failure",
            "request_class" to "VerifyTorrentRequest",
            "response_time_millis" to "123"
        )
    }

    @Test
    fun `test setTorrentsCount`() {
        // when
        analytics.setTorrentsCount(42)

        // then
        verify(analyticsProvider).setUserProperty("torrents_count", "41-50")
    }
}
