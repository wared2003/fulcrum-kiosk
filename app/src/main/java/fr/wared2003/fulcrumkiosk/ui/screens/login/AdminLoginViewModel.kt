package fr.wared2003.fulcrumkiosk.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.navigation.Screen
import fr.wared2003.fulcrumkiosk.domain.usecase.GetKioskConfigUseCase
import fr.wared2003.fulcrumkiosk.domain.usecase.VerifyAdminPinUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Admin Login screen.
 * Orchestrates the authentication process using domain Use Cases.
 */
class AdminLoginViewModel(
    private val verifyAdminPinUseCase: VerifyAdminPinUseCase,
    private val getKioskConfigUseCase: GetKioskConfigUseCase,
    private val navManager: NavManager
) : ViewModel() {

    private val _state = MutableStateFlow(AdminLoginState())
    val state = _state.asStateFlow()

    init {
        observeKioskConfig()
    }

    /**
     * Subscribes to kiosk configuration changes.
     * Updates the UI hint reactively if the PIN is set to default.
     */
    private fun observeKioskConfig() {
        getKioskConfigUseCase()
            .onEach { config ->
                _state.update {
                    it.copy(
                        isDefaultPinHintVisible = config.isDefaultAdminPin,
                        // We provide the default string "1234" only if the system detects it's the default.
                        // The actual secret PIN is never retrieved here.
                        defaultPin = if (config.isDefaultAdminPin) "1234" else ""
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: AdminLoginEvent) {
        when (event) {
            is AdminLoginEvent.OnPinChange -> {
                _state.update { it.copy(pin = event.newPin, isError = false) }
            }
            AdminLoginEvent.OnLoginClick -> {
                login()
            }
            AdminLoginEvent.OnBackToKiosk -> {
                navManager.navigate(Screen.Kiosk, Screen.Welcome, false)
            }
        }
    }

    /**
     * Validates the user input against the secure storage via VerifyAdminPinUseCase.
     */
    private fun login() {
        val inputPin = _state.value.pin

        // The comparison logic is delegated to the domain layer (Use Case -> Repository)
        if (verifyAdminPinUseCase(inputPin)) {
            _state.update { it.copy(isError = false, pin = "") }
            navManager.navigate(Screen.Settings, Screen.Welcome, false)
        } else {
            _state.update { it.copy(isError = true, pin = "") }
        }
    }
}