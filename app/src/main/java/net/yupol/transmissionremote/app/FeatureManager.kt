package net.yupol.transmissionremote.app

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig

class FeatureManager {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    init {
        remoteConfig.activate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                crashlytics.log("Remote config activated")
            } else {
                crashlytics.log("Remote config not activated")
            }
            logConfig()
        }
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {}

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: ${error.code}", error)
            }
        })
    }

    fun useOkHttp(): Boolean {
        return remoteConfig.getBoolean("use_okhttp")
    }

    private fun logConfig() {
        crashlytics.log("Use OkHttp: ${useOkHttp()}")
    }

    companion object {
        private val TAG = FeatureManager::class.java.simpleName
    }
}
