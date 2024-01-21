package net.yupol.transmissionremote.app.preferences.server

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.yupol.transmissionremote.app.logging.Logger
import net.yupol.transmissionremote.app.navigation.NavigationEvent
import net.yupol.transmissionremote.app.preferences.PreferencesRepository
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val preferencesRepository: PreferencesRepository,
    private val logger: Logger
) : ViewModel() {
    private val serverId: String? = savedStateHandle["server_id"]
    private val server = preferencesRepository.getServers().firstOrNull { it.id == serverId }

    private val _uiState = MutableStateFlow(
        ServerViewState(serverName = server?.name.orEmpty())
    )
    val uiState: StateFlow<ServerViewState> = _uiState.asStateFlow()

    private val _navigateTo = MutableStateFlow<NavigationEvent?>(null)
    val navigateTo: StateFlow<NavigationEvent?> = _navigateTo.asStateFlow()

    fun onBackClicked() {
        _navigateTo.value = NavigationEvent.NavigateUp()
    }

    fun onRemoveServerClicked() {
        if (server != null) {
            preferencesRepository.removeServer(server)
        } else {
            logger.log("Server is null when trying to remove server")
        }
        _navigateTo.value = NavigationEvent.NavigateUp()
    }

    fun onSaveClicked() {
        _navigateTo.value = NavigationEvent.NavigateUp()
    }
}
