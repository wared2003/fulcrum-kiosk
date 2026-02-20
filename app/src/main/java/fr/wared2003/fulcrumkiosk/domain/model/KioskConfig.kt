package fr.wared2003.fulcrumkiosk.domain.model

/**
 * Represents the core configuration for the kiosk.
 * This class is part of the domain layer and contains security states
 * without ever exposing sensitive data (PINs) in plain text.
 *
 * @property url The URL of the Progressive Web App (PWA) to display. Null if not configured.
 * @property isDefaultAdminPin Indicates if the administrator PIN is still set to the default value (unsecured).
 * @property isKioskPinSet Indicates if a specific PIN has been configured to exit the Kiosk mode.
 * @property brightness The screen brightness level (0f to 1f).
 * @property isAutoBrightness Indicates if auto brightness is enabled.
 * @property autoBrightnessMin The minimum brightness for auto-mode.
 * @property autoBrightnessMax The maximum brightness for auto-mode.
 * @property powerSavingDelayMinutes The delay in minutes before power saving is triggered.
 * @property powerSavingAction The action to perform when power saving is triggered (e.g., "dim", "off").
 * @property powerSavingDimValue The brightness value to use when dimming the screen.
 * @property isDimLockEnabled Overrides the power saving action to lock the device instead.
 * @property launchOnBoot Indicates if the application should be launched on boot.
 */
data class KioskConfig(
    val url: String?,
    val isDefaultAdminPin: Boolean,
    val isKioskPinSet: Boolean,
    val isLockOn: Boolean,
    val brightness: Float,
    val isAutoBrightness: Boolean,
    val autoBrightnessMin: Float,
    val autoBrightnessMax: Float,
    val powerSavingDelayMinutes: Int,
    val powerSavingAction: String,
    val powerSavingDimValue: Float,
    val isDimLockEnabled: Boolean,
    val launchOnBoot: Boolean
)
