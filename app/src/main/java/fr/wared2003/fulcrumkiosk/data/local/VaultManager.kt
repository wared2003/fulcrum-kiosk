package fr.wared2003.fulcrumkiosk.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import androidx.core.content.edit

/**
 * Manages sensitive data using EncryptedSharedPreferences for secure storage.
 * Exposes reactive state indicators (Boolean Flows) to maintain security
 * without exposing plain-text PINs to the rest of the application.
 */
class VaultManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private object PrefKeys {
        const val ADMIN_PIN = "admin_pin"
        const val KIOSK_PIN = "kiosk_PIN"
    }

    private object Defaults {
        const val ADMIN_PIN = "1234"
    }

    // --- PRIVATE READERS (Encapsulated for security) ---

    private fun getAdminPin(): String {
        return sharedPreferences.getString(PrefKeys.ADMIN_PIN, Defaults.ADMIN_PIN) ?: Defaults.ADMIN_PIN
    }

    private fun getKioskPin(): String {
        return sharedPreferences.getString(PrefKeys.KIOSK_PIN, null) ?: ""
    }

    // --- EXPOSED REACTIVE STATES (Flows) ---

    /**
     * Flow indicating if the Admin PIN is still set to the default value (1234).
     * Emits the current state immediately and updates on every change.
     */
    val isDefaultAdminPinFlow: Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PrefKeys.ADMIN_PIN) trySend(isDefaulAdmintPin())
        }
        trySend(isDefaulAdmintPin())
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    /**
     * Flow indicating if a Kiosk PIN has been configured.
     * Emits the current state immediately and updates on every change.
     */
    val isKioskPinSetFlow: Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PrefKeys.KIOSK_PIN) trySend(isKioskPinSet())
        }
        trySend(isKioskPinSet())
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    // --- BUSINESS LOGIC ---

    /**
     * Returns true if no PIN is stored or if the stored PIN matches the default.
     */
    fun isDefaulAdmintPin(): Boolean {
        val stored = sharedPreferences.getString(PrefKeys.ADMIN_PIN, null)
        return stored == null || stored == Defaults.ADMIN_PIN
    }

    /**
     * Checks if a Kiosk Exit PIN exists in the encrypted storage.
     */
    fun isKioskPinSet(): Boolean {
        return sharedPreferences.getString(PrefKeys.KIOSK_PIN, null) != null
    }

    /**
     * Validates a user-input PIN against the stored Admin secret.
     */
    fun verifyAdminPin(input: String): Boolean {
        return getAdminPin() == input
    }

    /**
     * Validates a user-input PIN against the stored Kiosk exit secret.
     */
    fun verifyKioskPin(input: String): Boolean {
        return getKioskPin() == input
    }

    // --- WRITE OPERATIONS ---

    /**
     * Persists a new Admin PIN to encrypted storage.
     */
    fun saveAdminPin(pin: String) {
        sharedPreferences.edit { putString(PrefKeys.ADMIN_PIN, pin) }
    }

    /**
     * Persists a new Kiosk Exit PIN to encrypted storage.
     */
    fun saveKioskPin(pin: String) {
        sharedPreferences.edit { putString(PrefKeys.KIOSK_PIN, pin) }
    }

    /**
     * Removes the Kiosk Exit PIN, disabling the PIN requirement for exit.
     */
     fun clearKioskPin() {
        sharedPreferences.edit(commit = true) {
            remove(PrefKeys.KIOSK_PIN)
        }
    }
}