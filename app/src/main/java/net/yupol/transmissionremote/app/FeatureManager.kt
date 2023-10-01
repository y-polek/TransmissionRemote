package net.yupol.transmissionremote.app

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class FeatureManager {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private var isInitialized = false

    init {
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                fetchTimeoutInSeconds = 10
            }
        )

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            isInitialized = true
        }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate()
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: ${error.code}", error)
            }
        })
    }

    fun isInitialized(): Boolean = isInitialized


    fun useOkHttp(): Boolean {
        return remoteConfig.getBoolean("use_okhttp")
    }

    companion object {
        private val TAG = FeatureManager::class.java.simpleName
    }
}
