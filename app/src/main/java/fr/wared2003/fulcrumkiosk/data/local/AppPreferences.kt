package fr.wared2003.fulcrumkiosk.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages non-sensitive application preferences using Jetpack DataStore.
 *
 * @param context The application context.
 */
class AppPreferences(
    private val context: Context,
) {

    // Expose a singleton instance of DataStore for the app.
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private object PreferencesKeys {
        val PWA_URL = stringPreferencesKey("pwa_url")
        val IS_LOCK_ON = booleanPreferencesKey("is_lock_on")
        val BRIGHTNESS = floatPreferencesKey("brightness")
        val IS_AUTO_BRIGHTNESS = booleanPreferencesKey("is_auto_brightness")
    }

    /**
     * A flow that emits the saved PWA URL whenever it changes.
     */
    val urlFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.PWA_URL]
        }

    /**
     * A flow that emits the saved LockOnState whenever it changes.
     */
    val isLockOnState: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.IS_LOCK_ON] == true }

    /**
     * A flow that emits the saved brightness level whenever it changes.
     */
    val brightnessFlow: Flow<Float> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.BRIGHTNESS] ?: 0.5f }

    /**
     * A flow that emits the saved auto-brightness state whenever it changes.
     */
    val isAutoBrightnessFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[PreferencesKeys.IS_AUTO_BRIGHTNESS] ?: true }


    /**
     * Persists the PWA URL.
     *
     * @param url The URL to save.
     */
    suspend fun saveUrl(url: String) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.PWA_URL] = url
        }
    }

    /*
     * Persists the lock state.
     *
     * @param isLockOn The lock state to save.
     */
    suspend fun saveIsLockOn(isLockOn: Boolean) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.IS_LOCK_ON] = isLockOn
        }
    }

    /**
     * Persists the brightness level.
     *
     * @param brightness The brightness level to save.
     */
    suspend fun saveBrightness(brightness: Float) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.BRIGHTNESS] = brightness
        }
    }

    /**
     * Persists the auto-brightness state.
     *
     * @param isAutoBrightness The auto-brightness state to save.
     */
    suspend fun saveIsAutoBrightness(isAutoBrightness: Boolean) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.IS_AUTO_BRIGHTNESS] = isAutoBrightness
        }
    }
}
