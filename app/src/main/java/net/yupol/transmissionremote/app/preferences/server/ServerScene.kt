package net.yupol.transmissionremote.app.preferences.server

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ServerScene(serverId: String?) {
    Text(text = "Server: $serverId")
}
