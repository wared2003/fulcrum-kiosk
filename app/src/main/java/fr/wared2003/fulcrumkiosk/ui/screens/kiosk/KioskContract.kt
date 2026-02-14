package fr.wared2003.fulcrumkiosk.ui.screens.kiosk

data class KioskState(
    val url: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isFullScreen: Boolean = false,
)

sealed interface KioskEvent {
    data object OnPageFinished : KioskEvent
    data class OnError(val message: String) : KioskEvent
    data object OnSecretBtnClicked : KioskEvent
}
