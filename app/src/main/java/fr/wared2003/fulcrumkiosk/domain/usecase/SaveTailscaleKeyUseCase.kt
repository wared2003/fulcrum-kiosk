package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

/**
 * A use case to save the Tailscale key.
 * It invokes the repository to persist the data securely.
 */
class SaveTailscaleKeyUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(key: String) = settingsRepository.saveTailscaleKey(key)
}
