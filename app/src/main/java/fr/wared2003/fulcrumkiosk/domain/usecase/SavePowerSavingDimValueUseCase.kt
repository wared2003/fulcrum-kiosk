package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

class SavePowerSavingDimValueUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(value: Float) {
        repository.savePowerSavingDimValue(value)
    }
}
