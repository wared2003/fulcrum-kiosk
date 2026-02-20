package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

/**
 * Use case to securely update or set the Kiosk exit PIN.
 * Includes business validation rules for the Kiosk security layer.
 */
class SaveKioskPinUseCase(
    private val repository: SettingsRepository
) {
    /**
     * Validates and saves the Kiosk PIN.
     * @param newPin The PIN string provided by the user.
     * @throws IllegalArgumentException if the PIN doesn't meet the 4-digit requirement.
     */
    suspend operator fun invoke(newPin: String) {
        // Business Rule: Kiosk PIN must be exactly 4 digits
        if (newPin.length != 4) {
            throw IllegalArgumentException("The PIN must be exactly 4 digits long.")
        }

        // Business Rule: PIN must contain only numbers
        if (!newPin.all { it.isDigit() }) {
            throw IllegalArgumentException("The PIN must only contain numeric digits.")
        }

        repository.saveKioskPin(newPin)
    }
}