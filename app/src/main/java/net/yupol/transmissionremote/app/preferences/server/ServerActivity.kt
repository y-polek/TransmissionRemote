package net.yupol.transmissionremote.app.preferences.server

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServersScene()
        }
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, ServerActivity::class.java)
        }
    }
}
