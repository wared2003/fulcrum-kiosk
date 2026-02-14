package fr.wared2003.fulcrumkiosk.ui.screens.login

import androidx.lifecycle.ViewModel
import fr.wared2003.fulcrumkiosk.data.local.VaultManager
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdminLoginViewModel(
    private val vaultManager: VaultManager,
    private val navManager: NavManager
) : ViewModel() {

    private val _state = MutableStateFlow(AdminLoginState())
    val state = _state.asStateFlow()

    init {
        // Check if the PIN is the default one to show a hint
        _state.update {
            it.copy(
                isDefaultPinHintVisible = vaultManager.isDefaultPin(),
                defaultPin = vaultManager.getAdminPin()
            )
        }
    }

    fun onEvent(event: AdminLoginEvent) {
        when (event) {
            is AdminLoginEvent.OnPinChange -> {
                _state.update { it.copy(pin = event.newPin, isError = false) }
            }
            AdminLoginEvent.OnLoginClick -> {
                login()
            }
        }
    }

    private fun login() {
        val correctPin = vaultManager.getAdminPin()
        if (_state.value.pin == correctPin) {
            _state.update { it.copy(isError = false, pin = "") }
            navManager.navigate(Screen.Settings, Screen.Welcome, false)
        } else {
            _state.update { it.copy(isError = true, pin = "") }
        }
    }
}
