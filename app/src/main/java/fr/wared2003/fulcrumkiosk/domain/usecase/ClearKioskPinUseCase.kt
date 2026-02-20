package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

/**
 * Use case to remove the Kiosk PIN protection.
 */
class ClearKioskPinUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() {
        repository.clearKioskPin()
    }
}