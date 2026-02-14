package fr.wared2003.fulcrumkiosk.data

import android.content.Context

class ConfigManager(context: Context) {
    private val prefs = context.getSharedPreferences("fulcrum_config", Context.MODE_PRIVATE)

    fun hasUrl(): Boolean = !getUrl().isNullOrBlank()

    fun saveUrl(url: String) {
        prefs.edit().putString("pwa_url", url).apply()
    }

    fun getUrl(): String? = prefs.getString("pwa_url", null)
}