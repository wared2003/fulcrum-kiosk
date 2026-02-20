package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

class SaveAutoBrightnessMinUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(min: Float) {
        repository.saveAutoBrightnessMin(min)
    }
}
