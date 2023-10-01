package net.yupol.transmissionremote.app.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent

class FirebaseAnalyticsProvider(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsProvider {
    override fun logEvent(name: String, vararg params: Pair<String, String>) {
        firebaseAnalytics.logEvent(name) {
            for ((key, value) in params) {
                param(key, value)
            }
        }
    }

    override fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value)
    }
}
