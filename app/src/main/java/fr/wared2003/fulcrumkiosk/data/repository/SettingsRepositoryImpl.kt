package fr.wared2003.fulcrumkiosk.data.repository

import fr.wared2003.fulcrumkiosk.data.local.AppPreferences
import fr.wared2003.fulcrumkiosk.data.local.VaultManager
import fr.wared2003.fulcrumkiosk.domain.model.KioskConfig
import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Implementation of the SettingsRepository.
 * This class orchestrates data operations from local data sources (AppPreferences and VaultManager).
 *
 * @param appPreferences Manages non-sensitive settings.
 * @param vaultManager Manages sensitive, encrypted data.
 */
class SettingsRepositoryImpl(
    private val appPreferences: AppPreferences,
    private val vaultManager: VaultManager
) : SettingsRepository {

    /**
     * A flow that provides the latest KioskConfig by combining data from AppPreferences and VaultManager.
     * It respects the business rule: tailscaleKey is considered null if blank.
     */
    override val kioskConfig: Flow<KioskConfig> = combine(
        appPreferences.urlFlow,
    ) { (url) ->
        KioskConfig(
            url = url
        )
    }

    /**
     * Saves the PWA URL to AppPreferences.
     * @param url The URL to be saved.
     */
    override suspend fun saveUrl(url: String) {
        appPreferences.saveUrl(url)
    }

}
