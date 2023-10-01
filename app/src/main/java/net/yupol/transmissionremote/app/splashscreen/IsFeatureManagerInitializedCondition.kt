package net.yupol.transmissionremote.app.splashscreen

import androidx.core.splashscreen.SplashScreen
import net.yupol.transmissionremote.app.FeatureManager

class IsFeatureManagerInitializedCondition(
    private val featureManager: FeatureManager
) : SplashScreen.KeepOnScreenCondition {

    override fun shouldKeepOnScreen(): Boolean {
        return !featureManager.isInitialized()
    }
}
