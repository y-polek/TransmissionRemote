package net.yupol.transmissionremote.device.connectivity

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Connectivity @Inject constructor(context: Application) {

    private val manager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val contentResolver: ContentResolver by lazy { context.contentResolver }

    fun isConnected() = manager.activeNetworkInfo?.isConnectedOrConnecting ?: false

    fun isAirplaneModeOn(): Boolean {
        return if (Build.VERSION.SDK_INT >= 17) {
            Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
        } else {
            Settings.System.getInt(contentResolver, Settings.System.AIRPLANE_MODE_ON, 0) != 0
        }
    }
}
