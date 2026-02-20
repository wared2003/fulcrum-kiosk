package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

class SaveAutoBrightnessUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(isAuto: Boolean) {
        repository.saveIsAutoBrightness(isAuto)
    }
}
