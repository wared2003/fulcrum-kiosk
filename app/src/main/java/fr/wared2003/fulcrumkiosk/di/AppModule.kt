package fr.wared2003.fulcrumkiosk.di

import fr.wared2003.fulcrumkiosk.data.local.AppPreferences
import fr.wared2003.fulcrumkiosk.data.local.VaultManager
import fr.wared2003.fulcrumkiosk.data.repository.SettingsRepositoryImpl
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository
import fr.wared2003.fulcrumkiosk.domain.usecase.GetKioskConfigUseCase
import fr.wared2003.fulcrumkiosk.domain.usecase.ProcessCommandUseCase
import fr.wared2003.fulcrumkiosk.domain.usecase.SavePwaUrlUseCase
import fr.wared2003.fulcrumkiosk.ui.screens.kiosk.KioskViewModel
import fr.wared2003.fulcrumkiosk.ui.screens.login.AdminLoginViewModel
import fr.wared2003.fulcrumkiosk.ui.screens.settings.SettingsViewModel
import fr.wared2003.fulcrumkiosk.ui.screens.welcome.WelcomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for the main application.
 * This module defines the dependencies for all layers.
 */
val appModule = module {
    // --- DATA LAYER ---
    single { AppPreferences(androidContext()) }
    single { VaultManager(androidContext()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get(), get()) }

    // --- DOMAIN LAYER ---
    single { NavManager() }

    // Use Cases
    factory { GetKioskConfigUseCase(get()) }
    factory { SavePwaUrlUseCase(get()) }
    factory { ProcessCommandUseCase(get()) }

    // --- PRESENTATION LAYER (VIEW MODELS) ---
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { AdminLoginViewModel(get(), get()) }
    viewModel { WelcomeViewModel(get()) }
    viewModel { KioskViewModel(get(), get()) }
}
