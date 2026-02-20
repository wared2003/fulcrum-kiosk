package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

class SaveAutoBrightnessMaxUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(max: Float) {
        repository.saveAutoBrightnessMax(max)
    }
}
