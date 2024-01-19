package net.yupol.transmissionremote.app.preferences.server

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.yupol.transmissionremote.app.preferences.PreferencesRepository
import net.yupol.transmissionremote.app.server.Server
import javax.inject.Inject

@HiltViewModel
class ServersListViewModel @Inject constructor(
    preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ServersListViewState(
            servers = preferencesRepository.getServers(),
            selectedServerId = preferencesRepository.getActiveServer()?.id
        )
    )
    val uiState: StateFlow<ServersListViewState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            servers = preferencesRepository.getServers()
        )
    }

    fun onAddServerClicked() {

    }

    fun onServerClicked(server: Server) {

    }

    fun onServerSelected(server: Server) {
        _uiState.value = _uiState.value.copy(
            selectedServerId = server.id
        )
    }
}
