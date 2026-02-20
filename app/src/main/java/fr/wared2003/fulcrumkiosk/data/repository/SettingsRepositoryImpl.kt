package fr.wared2003.fulcrumkiosk.data.repository

import android.util.Log
import fr.wared2003.fulcrumkiosk.data.local.AppPreferences
import fr.wared2003.fulcrumkiosk.data.local.VaultManager
import fr.wared2003.fulcrumkiosk.domain.model.KioskConfig
import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.runBlocking

/**
 * Implementation of [SettingsRepository].
 * Orchestrates data between non-sensitive storage (AppPreferences) and secure storage (VaultManager).
 */
class SettingsRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val vaultManager: VaultManager
) : SettingsRepository {

    /**
     * A flow that combines non-sensitive URL data with security state booleans.
     * The actual PINs are never exposed in this stream to maintain security principles.
     */
    override val kioskConfig: Flow<KioskConfig> = combine(
        appPreferences.urlFlow,
        vaultManager.isDefaultAdminPinFlow,
        vaultManager.isKioskPinSetFlow,
        appPreferences.isLockOnState
    ) { url, isDefault, isKioskSet, isLockOn ->
        Log.d("KioskDebug", "Source isLockOn: $isLockOn")
        KioskConfig(
            url = url,
            isLockOn = isLockOn,
            isDefaultAdminPin = isDefault,
            isKioskPinSet = isKioskSet
        )
    }


    /**
     * Saves the PWA target URL.
     */
    override suspend fun saveUrl(url: String) {
        appPreferences.saveUrl(url)
    }

    // --- ADMIN PIN OPERATIONS ---

    /**
     * Securely updates the administrator PIN via VaultManager.
     */
    override suspend fun saveAdminPin(newPin: String) {
        vaultManager.saveAdminPin(newPin)
    }

    /**
     * Validates the provided input against the stored Admin PIN.
     */
    override fun verifyAdminPin(input: String): Boolean {
        return vaultManager.verifyAdminPin(input)
    }

    // --- KIOSK PIN OPERATIONS ---

    /**
     * Securely updates the Kiosk open PIN.
     */
    override suspend fun saveKioskPin(newPin: String) {
        vaultManager.saveKioskPin(newPin)
    }

    /**
     * Validates the provided input against the stored Kiosk exit PIN.
     */
    override suspend fun verifyKioskPin(input: String): Boolean {
        return vaultManager.verifyKioskPin(input)
    }

    /**
     * Removes the Kiosk PIN, disabling the PIN requirement for to open kiosk.
     */
    override suspend fun clearKioskPin() {
        vaultManager.clearKioskPin()
    }

    /**
     * Save the Is LockOn settings (true or false)
     */
    override suspend fun saveIsLockOn(isLockOn: Boolean) {
        appPreferences.saveIsLockOn(isLockOn)
    }
}