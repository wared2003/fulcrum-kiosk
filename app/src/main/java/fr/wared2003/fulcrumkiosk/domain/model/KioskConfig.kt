package fr.wared2003.fulcrumkiosk.domain.model

/**
 * Represents the core configuration for the kiosk.
 * This data class is part of the domain layer and is UI-agnostic.
 *
 * @property url The URL of the Progressive Web App (PWA) to display. Null if not set.
 *
 */
data class KioskConfig(
    val url: String?
)
