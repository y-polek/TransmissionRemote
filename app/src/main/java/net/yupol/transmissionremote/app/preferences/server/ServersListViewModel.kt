package net.yupol.transmissionremote.app.preferences.server

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.yupol.transmissionremote.app.navigation.NavigationEvent
import net.yupol.transmissionremote.app.preferences.PreferencesRepository
import net.yupol.transmissionremote.app.server.Server
import javax.inject.Inject

@HiltViewModel
class ServersListViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ServersListViewState(
            servers = preferencesRepository.getServers(),
            selectedServerId = preferencesRepository.getActiveServer()?.id
        )
    )
    val uiState: StateFlow<ServersListViewState> = _uiState.asStateFlow()

    private val _navigateTo = MutableStateFlow<NavigationEvent?>(null)
    val navigateTo: StateFlow<NavigationEvent?> = _navigateTo.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            servers = preferencesRepository.getServers()
        )
    }

    fun onBackClicked() {
        _navigateTo.value = NavigationEvent.NavigateUp()
    }

    fun onAddServerClicked() {
        _navigateTo.value = NavigationEvent.NavigateToRoute("servers/server?server_id=null")
    }

    fun onServerClicked(server: Server) {
        _navigateTo.value = NavigationEvent.NavigateToRoute("servers/server?server_id=${server.id}")
    }

    fun onServerSelected(server: Server) {
        _uiState.value = _uiState.value.copy(
            selectedServerId = server.id
        )
        preferencesRepository.setActiveServer(server)
    }
}
