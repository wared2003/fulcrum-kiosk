package fr.wared2003.fulcrumkiosk.ui.screens.settings

/**
 * Defines the state for the General Settings screen.
 */
data class SettingsState(
    val url: String = "",
    val isSaving: Boolean = false,
    val showUrlDialog: Boolean = false,
    val isLockOn: Boolean = false,

    //adminPin
    val newAdminPin: String = "",
    val isDefaultAdminPin: Boolean = true,
    val showAdminPinDialog: Boolean = false,
    val adminPinErrorMessage: String? = null,

    val newKioskPin: String = "",
    val isKioskPinSet: Boolean = true,
    val showKioskPinDialog: Boolean = false,
    val kioskPinErrorMessage: String? = null,

    // Display
    val brightness: Float = 0.5f,
    val isAutoBrightness: Boolean = true,
)

/**
 * Defines all possible user actions (events) that can occur on the Settings screen.
 */
sealed interface SettingsEvent {
    // PWA URL Events
    object OnPwaUrlClicked : SettingsEvent
    object OnDismissUrlDialog : SettingsEvent
    data class OnUrlChanged(val newUrl: String) : SettingsEvent
    object OnSaveUrlClicked : SettingsEvent

    // Navigation Events
    object OnExitSettingsClicked : SettingsEvent

    //security events
    //admin pin events
    object OnAdminPinClicked : SettingsEvent
    data class OnAdminPinChange(val newPin: String) : SettingsEvent
    object OnSaveAdminPinClicked : SettingsEvent
    object OnDismissAdminPinDialog : SettingsEvent

    //admin pin events
    object OnKioskPinClicked : SettingsEvent
    data class OnKioskPinChange(val newPin: String) : SettingsEvent
    object OnSaveKioskPinClicked : SettingsEvent
    object OnDismissKioskPinDialog : SettingsEvent
    object OnClearKioskPinClicked : SettingsEvent

    //lock events
    object OnClickLockMode : SettingsEvent

    // Display Events
    data class OnBrightnessChanged(val newBrightness: Float) : SettingsEvent
    data class OnAutoBrightnessChanged(val isAuto: Boolean) : SettingsEvent
}
