package net.yupol.transmissionremote.app.preferences.server

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.yupol.transmissionremote.app.preferences.PreferencesRepository
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val serverId: String? = savedStateHandle["server_id"]

    private val _uiState = MutableStateFlow(ServerViewState())
    val uiState: StateFlow<ServerViewState> = _uiState.asStateFlow()

    init {
        val server = preferencesRepository.getServers().firstOrNull { it.id == serverId }
        _uiState.value = _uiState.value.copy(
            serverName = server?.name.orEmpty()
        )
    }
}
