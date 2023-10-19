package net.yupol.transmissionremote.app.theme

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import net.yupol.transmissionremote.app.logging.Logger
import net.yupol.transmissionremote.app.preferences.PreferencesRepository
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val logger: Logger
) : ViewModel() {

    val nightMode = MutableLiveData<NightMode>()

    init {
        viewModelScope.launch {
            preferencesRepository.getNightMode()
                .catch { error ->
                    logger.log(error)
                }
                .collect { mode ->
                    nightMode.value = mode
                }
        }
    }

    fun onNightModeSelected(mode: NightMode) {
        viewModelScope.launch {
            preferencesRepository.setNightMode(mode)
        }
    }
}
