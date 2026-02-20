package fr.wared2003.fulcrumkiosk.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Manages sensitive data using EncryptedSharedPreferences for secure storage.
 *
 * @param context The application context.
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
    }

    private object Defaults {
        const val ADMIN_PIN = "1234"
    }

    fun getAdminPin(): String {
        return sharedPreferences.getString(PrefKeys.ADMIN_PIN, Defaults.ADMIN_PIN) ?: Defaults.ADMIN_PIN
    }

    fun isDefaultPin(): Boolean {
        return sharedPreferences.getString(PrefKeys.ADMIN_PIN, null) == null
    }

    fun saveAdminPin(pin: String) {
        with(sharedPreferences.edit()) {
            putString(PrefKeys.ADMIN_PIN, pin)
            apply()
        }
    }
}
