package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

/**
 * Use case to update the administrator PIN in secure storage.
 */
class SaveAdminPinUseCase(
    private val repository: SettingsRepository
) {
    /**
     * Updates the admin PIN.
     * * @param newPin The new PIN to be saved.
     */
    suspend operator fun invoke(newPin: String) {
        // Business Rule: We could add validation here (e.g., minimum length of 4)
        if (newPin.length >= 4) {
            repository.saveAdminPin(newPin)
        } else {
            // You could throw a custom exception here if needed
            throw IllegalArgumentException("PIN must be at least 4 digits")
        }
    }
}