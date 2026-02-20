package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

/**
 * Use case to verify the PIN entered when attempting to exit Kiosk mode.
 */
class VerifyKioskPinUseCase(
    private val repository: SettingsRepository
) {
    /**
     * Executes the verification process.
     * @param inputPin The PIN entered in the exit dialog.
     * @return True if the PIN is correct, false otherwise.
     */
   suspend operator fun invoke(inputPin: String): Boolean {
        if (inputPin.isBlank()) return false

        return repository.verifyKioskPin(inputPin)
    }
}