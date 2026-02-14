package fr.wared2003.fulcrumkiosk.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages non-sensitive application preferences using Jetpack DataStore.
 *
 * @param context The application context.
 */
class AppPreferences(private val context: Context) {

    // Expose a singleton instance of DataStore for the app.
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private object PreferencesKeys {
        val PWA_URL = stringPreferencesKey("pwa_url")
    }

    /**
     * A flow that emits the saved PWA URL whenever it changes.
     */
    val urlFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.PWA_URL]
        }

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
}
