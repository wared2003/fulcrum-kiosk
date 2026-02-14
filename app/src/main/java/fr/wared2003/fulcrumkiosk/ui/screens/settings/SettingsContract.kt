package fr.wared2003.fulcrumkiosk.ui.screens.settings

/**
 * Defines the state for the General Settings screen.
 */
data class SettingsState(
    val url: String = "",
    val tailscaleKey: String = "",
    val isSaving: Boolean = false,
    val showUrlDialog: Boolean = false,
    val showTailscaleDialog: Boolean = false
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

    // Tailscale Key Events
    object OnTailscaleKeyClicked : SettingsEvent
    object OnDismissTailscaleDialog : SettingsEvent
    data class OnTailscaleKeyChanged(val newKey: String) : SettingsEvent
    object OnSaveTailscaleKeyClicked : SettingsEvent

    // Navigation Events
    object OnExitSettingsClicked : SettingsEvent
}
