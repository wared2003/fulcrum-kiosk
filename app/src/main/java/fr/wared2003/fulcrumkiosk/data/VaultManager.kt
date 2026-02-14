package fr.wared2003.fulcrumkiosk.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manager responsible for handling sensitive data using Hardware-backed encryption.
 */
class VaultManager(context: Context) {

    // 1. Initialize the Master Key for AES-256 encryption
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // 2. Setup EncryptedSharedPreferences for secure local storage
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "fulcrum_secure_vault",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Constraint [2026-02-11]: Check if Tailscale key is present.
     * Prevents connection if the key is missing.
     */
    fun hasTailscaleKey(): Boolean {
        return !getTailscaleKey().isNullOrBlank()
    }

    fun saveTailscaleKey(key: String) {
        sharedPreferences.edit().putString("ts_key", key).apply()
    }

    fun getTailscaleKey(): String? {
        return sharedPreferences.getString("ts_key", null)
    }

    /**
     * Admin PIN Management
     * Default value is "1234"
     */
    fun getAdminPin(): String {
        return sharedPreferences.getString("admin_pin", "1234") ?: "1234"
    }

    fun saveAdminPin(newPin: String) {
        sharedPreferences.edit().putString("admin_pin", newPin).apply()
    }

    fun isDefaultPin(): Boolean = getAdminPin() == "1234"

    /**
     * Clear all sensitive data (Factory Reset logic)
     */
    fun clearVault() {
        sharedPreferences.edit().clear().apply()
    }
}