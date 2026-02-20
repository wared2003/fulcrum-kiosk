package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

/**
 * Use case to validate the administrator PIN.
 * This encapsulates the business logic for authentication without exposing the actual PIN.
 */
class VerifyAdminPinUseCase(
    private val repository: SettingsRepository
) {
    /**
     * Executes the PIN verification.
     * * @param inputPin The PIN entered by the user.
     * @return True if the PIN is valid, false otherwise.
     */
    operator fun invoke(inputPin: String): Boolean {
        // Business Rule: A blank input is immediately invalid
        if (inputPin.isBlank()) return false

        // Delegate the technical comparison to the repository
        return repository.verifyAdminPin(inputPin)
    }
}