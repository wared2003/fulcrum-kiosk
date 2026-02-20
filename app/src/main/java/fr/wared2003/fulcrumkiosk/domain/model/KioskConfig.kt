package fr.wared2003.fulcrumkiosk.domain.model

/**
 * Represents the core configuration for the kiosk.
 * This class is part of the domain layer and contains security states
 * without ever exposing sensitive data (PINs) in plain text.
 *
 * @property url The URL of the Progressive Web App (PWA) to display. Null if not configured.
 * @property isDefaultAdminPin Indicates if the administrator PIN is still set to the default value (unsecured).
 * @property isKioskPinSet Indicates if a specific PIN has been configured to exit the Kiosk mode.
 */
data class KioskConfig(
    val url: String?,
    val isDefaultAdminPin: Boolean,
    val isKioskPinSet: Boolean,
    val isLockOn: Boolean
)
