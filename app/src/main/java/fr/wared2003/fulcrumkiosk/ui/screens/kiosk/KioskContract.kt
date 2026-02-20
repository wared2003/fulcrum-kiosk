package fr.wared2003.fulcrumkiosk.ui.screens.kiosk

data class KioskState(
    val url: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isLockOn: Boolean = false,
    val isFullScreen: Boolean = false,
    val brightness: Float = 0.5f,
    val isAutoBrightness: Boolean = true,
    val powerSavingDelayMinutes: Int = 5,
    val powerSavingAction: String = "dim",
    val powerSavingDimValue: Float = 0.1f,
    val isDimLockEnabled: Boolean = false
)

sealed interface KioskEvent {
    data object OnPageFinished : KioskEvent
    data class OnError(val message: String) : KioskEvent
    data object OnSecretBtnClicked : KioskEvent
    data object OnInactive : KioskEvent
}