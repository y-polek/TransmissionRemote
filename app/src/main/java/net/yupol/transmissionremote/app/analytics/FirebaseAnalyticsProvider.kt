package net.yupol.transmissionremote.app.analytics

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseAnalyticsProvider @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsProvider {
    override fun logEvent(name: String, vararg params: Pair<String, Any?>) {
        firebaseAnalytics.logEvent(name, bundleOf(*params))
    }

    override fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value)
    }
}
