package net.yupol.transmissionremote.app.preferences.server

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text

class ServerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("ServerActivity")
        }
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, ServerActivity::class.java)
        }
    }
}
