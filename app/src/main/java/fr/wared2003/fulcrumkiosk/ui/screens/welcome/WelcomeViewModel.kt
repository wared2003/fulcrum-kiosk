package fr.wared2003.fulcrumkiosk.ui.screens.welcome

import androidx.lifecycle.ViewModel
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager
import fr.wared2003.fulcrumkiosk.domain.navigation.Screen

class WelcomeViewModel(private val navManager: NavManager) : ViewModel() {

    fun onEvent(event: WelcomeEvent) {
        when (event) {
            WelcomeEvent.OnConfigureClick -> {
                navManager.navigate(Screen.Login)
            }
        }
    }
}
