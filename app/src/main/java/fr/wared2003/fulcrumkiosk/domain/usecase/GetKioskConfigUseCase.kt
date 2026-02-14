package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

/**
 * A use case to get the kiosk configuration flow.
 * This abstracts the data source from the ViewModel.
 */
class GetKioskConfigUseCase(private val settingsRepository: SettingsRepository) {
    operator fun invoke() = settingsRepository.kioskConfig
}
