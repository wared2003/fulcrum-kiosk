package fr.wared2003.fulcrumkiosk.ui.screens.kiosk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.navigation.Screen
import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KioskViewModel(
    private val repository: SettingsRepository,
    private val navManager: NavManager
) : ViewModel() {

    private val _state = MutableStateFlow(KioskState())
    val state = _state.asStateFlow()

    private var clickCount = 0
    private var lastClickTime = 0L

    init {
        observeConfig()
    }

    private fun observeConfig() {
        viewModelScope.launch {
            repository.kioskConfig.collect { config ->
                val urlNonNull = config.url ?: ""
                if (urlNonNull.isEmpty()){
                    navManager.navigate(Screen.Welcome, popUpTo = Screen.Settings, inclusive = true)
                    return@collect
                }

                _state.update { it.copy(
                    url = urlNonNull,
                    isLoading = urlNonNull.isNotEmpty(),
                    isFullScreen = true,
                    isLockOn = config.isLockOn,
                    brightness = config.brightness,
                    isAutoBrightness = config.isAutoBrightness
                ) }
            }
        }
    }

    fun onEvent(event: KioskEvent) {
        when (event) {
            is KioskEvent.OnPageFinished -> _state.update { it.copy(isLoading = false) }
            is KioskEvent.OnError -> _state.update { it.copy(error = event.message, isLoading = false)}
            is KioskEvent.OnSecretBtnClicked -> handleSecretBtn()

        }
    }

    fun handleSecretBtn() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastClickTime > 800) {
            clickCount = 0
        }

        clickCount++
        lastClickTime = currentTime

        if (clickCount >= 10) {
            clickCount = 0
            viewModelScope.launch {
                navManager.navigate(Screen.Login)
            }
        }
    }
}