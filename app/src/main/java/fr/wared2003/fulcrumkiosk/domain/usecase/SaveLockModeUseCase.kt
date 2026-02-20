package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository

class SaveLockModeUseCase(private val repository: SettingsRepository){

    suspend operator fun invoke(isLockOn: Boolean) {
        repository.saveIsLockOn(isLockOn)
    }
}