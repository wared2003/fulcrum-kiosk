package fr.wared2003.fulcrumkiosk

import android.app.Application
import fr.wared2003.fulcrumkiosk.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Custom Application class to initialize Koin for dependency injection.
 */
class FulcrumKioskApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin {
            // Provide the Android context to Koin
            androidContext(this@FulcrumKioskApp)
            // Load the application modules
            modules(appModule)
        }
    }
}
