package fr.wared2003.fulcrumkiosk.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
        val AUTO_BRIGHTNESS_MIN = floatPreferencesKey("auto_brightness_min")
        val AUTO_BRIGHTNESS_MAX = floatPreferencesKey("auto_brightness_max")
        val POWER_SAVING_DELAY_MINUTES = intPreferencesKey("power_saving_delay_minutes")
        val POWER_SAVING_ACTION = stringPreferencesKey("power_saving_action")
        val POWER_SAVING_DIM_VALUE = floatPreferencesKey("power_saving_dim_value")
        val IS_DIM_LOCK_ENABLED = booleanPreferencesKey("is_dim_lock_enabled")
        val LAUNCH_ON_BOOT = booleanPreferencesKey("launch_on_boot")
    }

    val urlFlow: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.PWA_URL] }
    val isLockOnState: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.IS_LOCK_ON] ?: false }
    val brightnessFlow: Flow<Float> = context.dataStore.data.map { it[PreferencesKeys.BRIGHTNESS] ?: 0.5f }
    val isAutoBrightnessFlow: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.IS_AUTO_BRIGHTNESS] ?: true }
    val autoBrightnessMinFlow: Flow<Float> = context.dataStore.data.map { it[PreferencesKeys.AUTO_BRIGHTNESS_MIN] ?: 0.1f }
    val autoBrightnessMaxFlow: Flow<Float> = context.dataStore.data.map { it[PreferencesKeys.AUTO_BRIGHTNESS_MAX] ?: 1.0f }
    val powerSavingDelayMinutesFlow: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.POWER_SAVING_DELAY_MINUTES] ?: 5 }
    val powerSavingActionFlow: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.POWER_SAVING_ACTION] ?: "dim" }
    val powerSavingDimValueFlow: Flow<Float> = context.dataStore.data.map { it[PreferencesKeys.POWER_SAVING_DIM_VALUE] ?: 0.1f }
    val isDimLockEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.IS_DIM_LOCK_ENABLED] ?: false }
    val launchOnBootFlow: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.LAUNCH_ON_BOOT] ?: false }

    suspend fun saveUrl(url: String) {
        context.dataStore.edit { it[PreferencesKeys.PWA_URL] = url }
    }

    suspend fun saveIsLockOn(isLockOn: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_LOCK_ON] = isLockOn }
    }

    suspend fun saveBrightness(brightness: Float) {
        context.dataStore.edit { it[PreferencesKeys.BRIGHTNESS] = brightness }
    }

    suspend fun saveIsAutoBrightness(isAutoBrightness: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_AUTO_BRIGHTNESS] = isAutoBrightness }
    }

    suspend fun saveAutoBrightnessMin(min: Float) {
        context.dataStore.edit { it[PreferencesKeys.AUTO_BRIGHTNESS_MIN] = min }
    }

    suspend fun saveAutoBrightnessMax(max: Float) {
        context.dataStore.edit { it[PreferencesKeys.AUTO_BRIGHTNESS_MAX] = max }
    }

    suspend fun savePowerSavingDelayMinutes(delay: Int) {
        context.dataStore.edit { it[PreferencesKeys.POWER_SAVING_DELAY_MINUTES] = delay }
    }

    suspend fun savePowerSavingAction(action: String) {
        context.dataStore.edit { it[PreferencesKeys.POWER_SAVING_ACTION] = action }
    }

    suspend fun savePowerSavingDimValue(value: Float) {
        context.dataStore.edit { it[PreferencesKeys.POWER_SAVING_DIM_VALUE] = value }
    }

    suspend fun saveIsDimLockEnabled(isEnabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_DIM_LOCK_ENABLED] = isEnabled }
    }

    suspend fun saveLaunchOnBoot(isEnabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.LAUNCH_ON_BOOT] = isEnabled }
    }
}