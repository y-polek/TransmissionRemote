package net.yupol.transmissionremote.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics.Event
import com.google.firebase.analytics.FirebaseAnalytics.Param
import net.yupol.transmissionremote.app.transport.request.Request

class Analytics(
    private val analyticsProvider: AnalyticsProvider
) {
    fun logScreenView(screenName: String, screenClass: Class<*>) {
        analyticsProvider.logEvent(
            name = Event.SCREEN_VIEW,
            Param.SCREEN_NAME to screenName,
            Param.SCREEN_CLASS to screenClass.simpleName
        )
    }

    fun <T> logOkHttpRequestStart(requestClass: Class<T>) where T : Request<*> {
        analyticsProvider.logEvent(
            name = EVENT_OKHTTP_REQUEST_START,
            PARAM_REQUEST_CLASS to requestClass.simpleName
        )
    }

    fun <T> logOkHttpRequestSuccess(
        requestClass: Class<T>,
        responseTimeMillis: Long
    ) where T : Request<*> {
        analyticsProvider.logEvent(
            name = EVENT_OKHTTP_REQUEST_SUCCESS,
            PARAM_REQUEST_CLASS to requestClass.simpleName,
            PARAM_RESPONSE_TIME_MILLIS to "$responseTimeMillis"
        )
    }

    fun <T> logOkHttpRequestFailure(
        requestClass: Class<T>,
        responseTimeMillis: Long
    ) where T : Request<*> {
        analyticsProvider.logEvent(
            name = EVENT_OKHTTP_REQUEST_FAILURE,
            PARAM_REQUEST_CLASS to requestClass.simpleName,
            PARAM_RESPONSE_TIME_MILLIS to "$responseTimeMillis"
        )
    }

    fun <T> logRobospiceRequestStart(requestClass: Class<T>) where T : Request<*> {
        analyticsProvider.logEvent(
            name = EVENT_ROBOSPICE_REQUEST_START,
            PARAM_REQUEST_CLASS to requestClass.simpleName
        )
    }

    fun <T> logRobospiceRequestSuccess(
        requestClass: Class<T>,
        responseTimeMillis: Long
    ) where T : Request<*> {
        analyticsProvider.logEvent(
            name = EVENT_ROBOSPICE_REQUEST_SUCCESS,
            PARAM_REQUEST_CLASS to requestClass.simpleName,
            PARAM_RESPONSE_TIME_MILLIS to "$responseTimeMillis"
        )
    }

    fun <T> logRobospiceRequestFailure(
        requestClass: Class<T>,
        responseTimeMillis: Long
    ) where T : Request<*> {
        analyticsProvider.logEvent(
            name = EVENT_ROBOSPICE_REQUEST_FAILURE,
            PARAM_REQUEST_CLASS to requestClass.simpleName,
            PARAM_RESPONSE_TIME_MILLIS to "$responseTimeMillis"
        )
    }

    fun setTorrentsCount(count: Int) {
        analyticsProvider.setUserProperty(PROPERTY_TORRENTS_COUNT, TorrentCount.fromCount(count).value)
    }

    companion object {
        private const val PROPERTY_TORRENTS_COUNT = "torrents_count"

        private const val EVENT_OKHTTP_REQUEST_START = "okhttp_request_start"
        private const val EVENT_OKHTTP_REQUEST_SUCCESS = "okhttp_request_success"
        private const val EVENT_OKHTTP_REQUEST_FAILURE = "okhttp_request_failure"

        private const val EVENT_ROBOSPICE_REQUEST_START = "robospice_request_start"
        private const val EVENT_ROBOSPICE_REQUEST_SUCCESS = "robospice_request_success"
        private const val EVENT_ROBOSPICE_REQUEST_FAILURE = "robospice_request_failure"

        private const val PARAM_REQUEST_CLASS = "request_class"
        private const val PARAM_RESPONSE_TIME_MILLIS = "response_time_millis"
    }
}
