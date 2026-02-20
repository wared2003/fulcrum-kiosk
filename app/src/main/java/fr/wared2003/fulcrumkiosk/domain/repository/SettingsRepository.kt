package fr.wared2003.fulcrumkiosk.domain.repository

import fr.wared2003.fulcrumkiosk.domain.model.KioskConfig
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for settings operations.
 * Located in the Domain layer to be used by Use Cases.
 */
interface SettingsRepository {
    /**
     * Observable flow of the kiosk configuration.
     */
    val kioskConfig: Flow<KioskConfig>

    /**
     * Persists the PWA URL.
     */
    suspend fun saveUrl(url: String)

    /**
     * Persists a new administrator PIN securely.
     */
    suspend fun saveAdminPin(newPin: String)

    /**
     * Checks if the provided input matches the stored administrator PIN.
     */
    fun verifyAdminPin(input: String): Boolean

    /**
     * Checks if the provided input matches the stored Kiosk lock PIN.
     */
    suspend fun saveKioskPin(newPin: String)

    /**
     * Validates the provided input against the stored Kiosk lock PIN.
     */
    suspend fun verifyKioskPin(input: String): Boolean

    /**
     * Removes the Kiosk lock PIN, disabling the PIN requirement to open Kiosk.
     */
    suspend fun clearKioskPin()
}
