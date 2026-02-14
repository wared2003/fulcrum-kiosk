package fr.wared2003.fulcrumkiosk.domain.repository

import fr.wared2003.fulcrumkiosk.domain.model.KioskConfig
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the repository that manages kiosk configuration settings.
 * This is part of the domain layer and defines the contract for data operations.
 */
interface SettingsRepository {
    /**
     * A hot flow that emits the current KioskConfig whenever it changes.
     * Consumers can collect this flow to react to configuration updates in real-time.
     */
    val kioskConfig: Flow<KioskConfig>

    /**
     * Persists the PWA URL.
     *
     * @param url The URL to save.
     */
    suspend fun saveUrl(url: String)

    /**
     * Persists the Tailscale authentication key.
     *
     * @param key The Tailscale key to save.
     */
    suspend fun saveTailscaleKey(key: String)
}
