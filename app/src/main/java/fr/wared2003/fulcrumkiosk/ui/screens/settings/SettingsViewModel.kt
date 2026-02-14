package fr.wared2003.fulcrumkiosk.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.navigation.Screen
import fr.wared2003.fulcrumkiosk.domain.usecase.GetKioskConfigUseCase
import fr.wared2003.fulcrumkiosk.domain.usecase.SavePwaUrlUseCase
import fr.wared2003.fulcrumkiosk.domain.usecase.SaveTailscaleKeyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getKioskConfigUseCase: GetKioskConfigUseCase,
    private val savePwaUrlUseCase: SavePwaUrlUseCase,
    private val saveTailscaleKeyUseCase: SaveTailscaleKeyUseCase,
    private val navManager: NavManager // Inject NavManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        getKioskConfigUseCase()
            .onEach { config ->
                _state.update {
                    it.copy(
                        url = config.url ?: "",
                        tailscaleKey = config.tailscaleKey ?: ""
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            // URL Events
            is SettingsEvent.OnUrlChanged -> _state.update { it.copy(url = event.newUrl) }
            is SettingsEvent.OnPwaUrlClicked -> _state.update { it.copy(showUrlDialog = true) }
            is SettingsEvent.OnDismissUrlDialog -> _state.update { it.copy(showUrlDialog = false) }
            is SettingsEvent.OnSaveUrlClicked -> saveUrl()

            // Tailscale Events
            is SettingsEvent.OnTailscaleKeyChanged -> _state.update { it.copy(tailscaleKey = event.newKey) }
            is SettingsEvent.OnTailscaleKeyClicked -> _state.update { it.copy(showTailscaleDialog = true) }
            is SettingsEvent.OnDismissTailscaleDialog -> _state.update { it.copy(showTailscaleDialog = false) }
            is SettingsEvent.OnSaveTailscaleKeyClicked -> saveTailscaleKey()

            // Navigation Event
            SettingsEvent.OnExitSettingsClicked -> {
                navManager.navigate(Screen.Kiosk, popUpTo = Screen.Settings, inclusive = true)
            }
        }
    }

    private fun saveUrl() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            if (state.value.url.startsWith("https://") || state.value.url.startsWith("http://") || state.value.url.isEmpty()) {
                savePwaUrlUseCase(state.value.url)
                _state.update { it.copy(showUrlDialog = false) }
            } else {
                // TODO: Handle URL validation error
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun saveTailscaleKey() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            saveTailscaleKeyUseCase(state.value.tailscaleKey)
            _state.update { it.copy(showTailscaleDialog = false, isSaving = false) }
        }
    }
}
