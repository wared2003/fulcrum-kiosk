package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

/**
 * A use case to save the PWA URL.
 * It invokes the repository to persist the data.
 */
class SavePwaUrlUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(url: String) = settingsRepository.saveUrl(url)
}
