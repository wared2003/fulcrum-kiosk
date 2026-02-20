package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

class SaveBrightnessUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(brightness: Float) {
        repository.saveBrightness(brightness)
    }
}
