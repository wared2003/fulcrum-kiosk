package fr.wared2003.fulcrumkiosk.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.navigation.Screen
import fr.wared2003.fulcrumkiosk.domain.usecase.ClearKioskPinUseCase
import fr.wared2003.fulcrumkiosk.domain.usecase.GetKioskConfigUseCase
import fr.wared2003.fulcrumkiosk.domain.usecase.SaveAdminPinUseCase
import fr.wared2003.fulcrumkiosk.domain.usecase.SaveKioskPinUseCase
import fr.wared2003.fulcrumkiosk.domain.usecase.SavePwaUrlUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getKioskConfigUseCase: GetKioskConfigUseCase,
    private val savePwaUrlUseCase: SavePwaUrlUseCase,
    private val navManager: NavManager,
    private val saveAdminPinUseCase: SaveAdminPinUseCase,
    private val saveKioskPinUseCase: SaveKioskPinUseCase,
    private val ClearKioskPinUseCase: ClearKioskPinUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        getKioskConfigUseCase()
            .onEach { config ->
                _state.update {
                    it.copy(
                        url = config.url ?: "",
                        isDefaultAdminPin = config.isDefaultAdminPin,
                        isKioskPinSet = config.isKioskPinSet
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


            // Navigation Event
            SettingsEvent.OnExitSettingsClicked -> {
                navManager.navigate(Screen.Kiosk, popUpTo = Screen.Settings, inclusive = true)
            }

            // Admin Pin Events
            SettingsEvent.OnAdminPinClicked -> {
                _state.update { it.copy(showAdminPinDialog = true, adminPinErrorMessage = null) }
            }
            is SettingsEvent.OnAdminPinChange -> {
                _state.update { it.copy(newAdminPin = event.newPin, adminPinErrorMessage = null) }
            }
            SettingsEvent.OnSaveAdminPinClicked -> {
                saveAdminPin()
            }
            SettingsEvent.OnDismissAdminPinDialog -> {
                _state.update { it.copy(showAdminPinDialog = false, newAdminPin = "", adminPinErrorMessage = null) }
            }

            //kiosk pin events
            SettingsEvent.OnKioskPinClicked -> {
                _state.update { it.copy(showKioskPinDialog = true, kioskPinErrorMessage = null) }
            }
            is SettingsEvent.OnKioskPinChange -> {
                _state.update { it.copy(newKioskPin = event.newPin, kioskPinErrorMessage = null) }
            }
            SettingsEvent.OnSaveKioskPinClicked -> {
                saveKioskPin()
            }
            SettingsEvent.OnDismissKioskPinDialog -> {
                _state.update { it.copy(showKioskPinDialog = false, newKioskPin = "", kioskPinErrorMessage = null) }
            }
            SettingsEvent.OnClearKioskPinClicked -> {
                clearKioskPin()
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

    /**
     * Saves the new Admin PIN with error handling.
     */
    private fun saveAdminPin() {
        viewModelScope.launch {
            try {
                saveAdminPinUseCase(_state.value.newAdminPin)
                // Success: Close dialog and reset local input state
                _state.update {
                    it.copy(
                        showAdminPinDialog = false,
                        newAdminPin = "",
                        adminPinErrorMessage = null
                    )
                }
            } catch (e: Exception) {
                // Failure: Keep dialog open and show the error (e.g., "PIN too short")
                _state.update { it.copy(adminPinErrorMessage = e.message) }
            }
        }
    }

    /**
     * Saves the new Kiosk PIN with error handling.
     */
    private fun saveKioskPin() {
        viewModelScope.launch {

            try {
                saveKioskPinUseCase(_state.value.newKioskPin)
                // Success: Close dialog and reset local input state
                _state.update {
                    it.copy(
                        showKioskPinDialog = false,
                        newKioskPin = "",
                        kioskPinErrorMessage = null
                    )
                }
            } catch (e: Exception) {
                // Failure: Keep dialog open and show the error (e.g., "PIN too short")
                _state.update { it.copy(kioskPinErrorMessage = e.message) }
            }
        }
    }

    /**
     * Remove Kiosk PIN.
     */
    private fun clearKioskPin() {
        viewModelScope.launch {
            ClearKioskPinUseCase()
            _state.update {
                it.copy(
                    showKioskPinDialog = false,
                    newKioskPin = "",
                    kioskPinErrorMessage = null
                )
            }
        }
    }
}
