package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

class SavePowerSavingDelayMinutesUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(delay: Int) {
        repository.savePowerSavingDelayMinutes(delay)
    }
}
