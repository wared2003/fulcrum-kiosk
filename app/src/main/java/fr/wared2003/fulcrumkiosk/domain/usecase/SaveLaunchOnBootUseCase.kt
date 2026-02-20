package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

class SaveLaunchOnBootUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(isEnabled: Boolean) {
        repository.saveLaunchOnBoot(isEnabled)
    }
}
