package fr.wared2003.fulcrumkiosk.ui.screens.kiosk

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.wared2003.fulcrumkiosk.MainActivity
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.navigation.Screen
import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KioskViewModel(
    private val repository: SettingsRepository,
    private val navManager: NavManager,
    private val context: Context
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
                    isAutoBrightness = config.isAutoBrightness,
                    powerSavingDelayMinutes = config.powerSavingDelayMinutes,
                    powerSavingAction = config.powerSavingAction,
                    powerSavingDimValue = config.powerSavingDimValue,
                    isDimLockEnabled = config.isDimLockEnabled
                ) }
            }
        }
    }

    fun onEvent(event: KioskEvent) {
        when (event) {
            is KioskEvent.OnPageFinished -> _state.update { it.copy(isLoading = false) }
            is KioskEvent.OnError -> _state.update { it.copy(error = event.message, isLoading = false)}
            is KioskEvent.OnSecretBtnClicked -> handleSecretBtn()
            is KioskEvent.OnInactive -> handleInactive()
        }
    }

    private fun handleInactive() {
        val window = (context as? Activity)?.window
        if (_state.value.isDimLockEnabled && isDeviceOwner()) {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            dpm.lockNow()
        } else {
            when (_state.value.powerSavingAction) {
                "dim" -> {
                    val attributes = window?.attributes
                    attributes?.screenBrightness = _state.value.powerSavingDimValue
                    window?.attributes = attributes
                }
                "off" -> {
                    val attributes = window?.attributes
                    attributes?.screenBrightness = 0.001f
                    window?.attributes = attributes
                }
            }
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

    private fun isDeviceOwner(): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return dpm.isDeviceOwnerApp(context.packageName)
    }
}