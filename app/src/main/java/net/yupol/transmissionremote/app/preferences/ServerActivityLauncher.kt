package net.yupol.transmissionremote.app.preferences

import android.content.Context
import android.content.Intent
import net.yupol.transmissionremote.app.FeatureManager
import net.yupol.transmissionremote.app.preferences.server.ServerActivity
import javax.inject.Inject

class ServerActivityLauncher @Inject constructor(
    private val featureManager: FeatureManager
) {
    fun launchServerActivity(context: Context) {
        val intent = if (featureManager.useLegacyServerActivity()) {
            Intent(context, LegacyServersActivity::class.java)
        } else {
            ServerActivity.intent(context)
        }
        context.startActivity(intent)
    }
}
