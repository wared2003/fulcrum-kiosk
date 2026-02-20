package fr.wared2003.fulcrumkiosk.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.navigation.Screen
import fr.wared2003.fulcrumkiosk.domain.usecase.*
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
    private val clearKioskPinUseCase: ClearKioskPinUseCase,
    private val saveLockModeUseCase: SaveLockModeUseCase,
    private val saveBrightnessUseCase: SaveBrightnessUseCase,
    private val saveAutoBrightnessUseCase: SaveAutoBrightnessUseCase,
    private val saveAutoBrightnessMinUseCase: SaveAutoBrightnessMinUseCase,
    private val saveAutoBrightnessMaxUseCase: SaveAutoBrightnessMaxUseCase,
    private val savePowerSavingDelayMinutesUseCase: SavePowerSavingDelayMinutesUseCase,
    private val savePowerSavingActionUseCase: SavePowerSavingActionUseCase,
    private val savePowerSavingDimValueUseCase: SavePowerSavingDimValueUseCase,
    private val saveIsDimLockEnabledUseCase: SaveIsDimLockEnabledUseCase,
    private val saveLaunchOnBootUseCase: SaveLaunchOnBootUseCase
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
                        isKioskPinSet = config.isKioskPinSet,
                        isLockOn = config.isLockOn,
                        brightness = config.brightness,
                        isAutoBrightness = config.isAutoBrightness,
                        autoBrightnessMin = config.autoBrightnessMin,
                        autoBrightnessMax = config.autoBrightnessMax,
                        powerSavingDelayMinutes = config.powerSavingDelayMinutes,
                        powerSavingAction = config.powerSavingAction,
                        powerSavingDimValue = config.powerSavingDimValue,
                        isDimLockEnabled = config.isDimLockEnabled,
                        launchOnBoot = config.launchOnBoot
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

            //security
            SettingsEvent.OnClickLockMode -> {
                toggleLockMode()
            }

            // Display Events
            is SettingsEvent.OnBrightnessChanged -> {
                viewModelScope.launch {
                    saveBrightnessUseCase(event.newBrightness)
                }
            }
            is SettingsEvent.OnAutoBrightnessChanged -> {
                viewModelScope.launch {
                    saveAutoBrightnessUseCase(event.isAuto)
                }
            }
            is SettingsEvent.OnAutoBrightnessMinChanged -> {
                viewModelScope.launch {
                    saveAutoBrightnessMinUseCase(event.newMin)
                }
            }
            is SettingsEvent.OnAutoBrightnessMaxChanged -> {
                viewModelScope.launch {
                    saveAutoBrightnessMaxUseCase(event.newMax)
                }
            }

            // Power Saving Events
            is SettingsEvent.OnPowerSavingDelayChanged -> {
                viewModelScope.launch {
                    savePowerSavingDelayMinutesUseCase(event.newDelay)
                }
            }
            is SettingsEvent.OnPowerSavingActionChanged -> {
                viewModelScope.launch {
                    savePowerSavingActionUseCase(event.newAction)
                }
            }
            is SettingsEvent.OnPowerSavingDimValueChanged -> {
                viewModelScope.launch {
                    savePowerSavingDimValueUseCase(event.newValue)
                }
            }
            is SettingsEvent.OnIsDimLockEnabledChanged -> {
                viewModelScope.launch {
                    saveIsDimLockEnabledUseCase(event.isEnabled)
                }
            }

            // General Events
            is SettingsEvent.OnLaunchOnBootChanged -> {
                viewModelScope.launch {
                    saveLaunchOnBootUseCase(event.isEnabled)
                }
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

    private fun saveAdminPin() {
        viewModelScope.launch {
            try {
                saveAdminPinUseCase(_state.value.newAdminPin)
                _state.update {
                    it.copy(
                        showAdminPinDialog = false,
                        newAdminPin = "",
                        adminPinErrorMessage = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(adminPinErrorMessage = e.message) }
            }
        }
    }

    private fun saveKioskPin() {
        viewModelScope.launch {

            try {
                saveKioskPinUseCase(_state.value.newKioskPin)
                _state.update {
                    it.copy(
                        showKioskPinDialog = false,
                        newKioskPin = "",
                        kioskPinErrorMessage = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(kioskPinErrorMessage = e.message) }
            }
        }
    }

    private fun clearKioskPin() {
        viewModelScope.launch {
            clearKioskPinUseCase()
            _state.update {
                it.copy(
                    showKioskPinDialog = false,
                    newKioskPin = "",
                    kioskPinErrorMessage = null
                )
            }
        }
    }

    private fun toggleLockMode() {
        viewModelScope.launch {
            val isLockOn = !_state.value.isLockOn
            saveLockModeUseCase(isLockOn)
        }
    }
}