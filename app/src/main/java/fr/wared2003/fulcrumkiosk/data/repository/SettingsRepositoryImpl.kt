package fr.wared2003.fulcrumkiosk.data.repository

import fr.wared2003.fulcrumkiosk.data.local.AppPreferences
import fr.wared2003.fulcrumkiosk.data.local.VaultManager
import fr.wared2003.fulcrumkiosk.domain.model.KioskConfig
import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SettingsRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val vaultManager: VaultManager
) : SettingsRepository {

    override val kioskConfig: Flow<KioskConfig> = combine(
        appPreferences.urlFlow,
        vaultManager.isDefaultAdminPinFlow,
        vaultManager.isKioskPinSetFlow,
        appPreferences.isLockOnState,
        appPreferences.brightnessFlow,
        appPreferences.isAutoBrightnessFlow,
        appPreferences.autoBrightnessMinFlow,
        appPreferences.autoBrightnessMaxFlow,
        appPreferences.powerSavingDelayMinutesFlow,
        appPreferences.powerSavingActionFlow,
        appPreferences.powerSavingDimValueFlow,
        appPreferences.isDimLockEnabledFlow
    ) { values ->
        KioskConfig(
            url = values[0] as? String,
            isDefaultAdminPin = values[1] as? Boolean ?: true,
            isKioskPinSet = values[2] as? Boolean ?: false,
            isLockOn = values[3] as? Boolean ?: false,
            brightness = (values[4] as? Number)?.toFloat() ?: 0.5f,
            isAutoBrightness = values[5] as? Boolean ?: true,
            autoBrightnessMin = (values[6] as? Number)?.toFloat() ?: 0.1f,
            autoBrightnessMax = (values[7] as? Number)?.toFloat() ?: 1.0f,
            powerSavingDelayMinutes = (values[8] as? Number)?.toInt() ?: 5,
            powerSavingAction = values[9] as? String ?: "dim",
            powerSavingDimValue = (values[10] as? Number)?.toFloat() ?: 0.1f,
            isDimLockEnabled = values[11] as? Boolean ?: false
        )
    }

    override suspend fun saveUrl(url: String) {
        appPreferences.saveUrl(url)
    }

    override suspend fun saveAdminPin(newPin: String) {
        vaultManager.saveAdminPin(newPin)
    }

    override fun verifyAdminPin(input: String): Boolean {
        return vaultManager.verifyAdminPin(input)
    }

    override suspend fun saveKioskPin(newPin: String) {
        vaultManager.saveKioskPin(newPin)
    }

    override suspend fun verifyKioskPin(input: String): Boolean {
        return vaultManager.verifyKioskPin(input)
    }

    override suspend fun clearKioskPin() {
        vaultManager.clearKioskPin()
    }

    override suspend fun saveIsLockOn(isLockOn: Boolean) {
        appPreferences.saveIsLockOn(isLockOn)
    }

    override suspend fun saveBrightness(brightness: Float) {
        appPreferences.saveBrightness(brightness)
    }

    override suspend fun saveIsAutoBrightness(isAutoBrightness: Boolean) {
        appPreferences.saveIsAutoBrightness(isAutoBrightness)
    }

    override suspend fun saveAutoBrightnessMin(min: Float) {
        appPreferences.saveAutoBrightnessMin(min)
    }

    override suspend fun saveAutoBrightnessMax(max: Float) {
        appPreferences.saveAutoBrightnessMax(max)
    }

    override suspend fun savePowerSavingDelayMinutes(delay: Int) {
        appPreferences.savePowerSavingDelayMinutes(delay)
    }

    override suspend fun savePowerSavingAction(action: String) {
        appPreferences.savePowerSavingAction(action)
    }

    override suspend fun savePowerSavingDimValue(value: Float) {
        appPreferences.savePowerSavingDimValue(value)
    }

    override suspend fun saveIsDimLockEnabled(isEnabled: Boolean) {
        appPreferences.saveIsDimLockEnabled(isEnabled)
    }
}