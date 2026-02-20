package fr.wared2003.fulcrumkiosk.ui.screens.kiosk

data class KioskState(
    val url: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isLockOn: Boolean = false,
    val isFullScreen: Boolean = false,
    val brightness: Float = 0.5f,
    val isAutoBrightness: Boolean = true,
)

sealed interface KioskEvent {
    data object OnPageFinished : KioskEvent
    data class OnError(val message: String) : KioskEvent
    data object OnSecretBtnClicked : KioskEvent
}
