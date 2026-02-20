package fr.wared2003.fulcrumkiosk.di

import fr.wared2003.fulcrumkiosk.data.local.AppPreferences
import fr.wared2003.fulcrumkiosk.data.local.VaultManager
import fr.wared2003.fulcrumkiosk.data.repository.SettingsRepositoryImpl
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.repository.SettingsRepository
import fr.wared2003.fulcrumkiosk.domain.usecase.*
import fr.wared2003.fulcrumkiosk.ui.screens.kiosk.KioskViewModel
import fr.wared2003.fulcrumkiosk.ui.screens.login.AdminLoginViewModel
import fr.wared2003.fulcrumkiosk.ui.screens.settings.SettingsViewModel
import fr.wared2003.fulcrumkiosk.ui.screens.welcome.WelcomeViewModel

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Main Koin DI module.
 * Uses Constructor DSL (viewModelOf, factoryOf) to avoid manual get() calls.
 */
val appModule = module {

    // --- DATA LAYER ---
    singleOf(::AppPreferences)
    singleOf(::VaultManager)

    // Bind the implementation to its interface
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class

    // --- DOMAIN LAYER ---
    singleOf(::NavManager)

    // Use Cases (Factory pattern)
    factoryOf(::GetKioskConfigUseCase)
    factoryOf(::SavePwaUrlUseCase)
    factoryOf(::ProcessCommandUseCase)
    factoryOf(::SaveAdminPinUseCase)
    factoryOf(::VerifyAdminPinUseCase)
    factoryOf(::ClearKioskPinUseCase)
    factoryOf(::SaveKioskPinUseCase)
    factoryOf(::SaveLockModeUseCase)
    factoryOf(::SaveBrightnessUseCase)
    factoryOf(::SaveAutoBrightnessUseCase)
    factoryOf(::SaveAutoBrightnessMinUseCase)
    factoryOf(::SaveAutoBrightnessMaxUseCase)

    // Admin PIN Use Cases
    factoryOf(::SaveAdminPinUseCase)
    factoryOf(::VerifyAdminPinUseCase)

    // Kiosk PIN Use Cases (Added for completeness)
    factoryOf(::SaveKioskPinUseCase)
    factoryOf(::VerifyKioskPinUseCase)
    factoryOf(::ClearKioskPinUseCase)


    // --- PRESENTATION LAYER (VIEW MODELS) ---
    // Koin automatically resolves dependencies for these constructors
    viewModelOf(::SettingsViewModel)
    viewModelOf(::AdminLoginViewModel)
    viewModelOf(::WelcomeViewModel)
    viewModelOf(::KioskViewModel)
}