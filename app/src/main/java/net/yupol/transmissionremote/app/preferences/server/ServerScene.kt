package net.yupol.transmissionremote.app.preferences.server

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ServerScene() {
    val viewModel: ServerViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    Text(text = "Server: ${uiState.serverName}")
}
