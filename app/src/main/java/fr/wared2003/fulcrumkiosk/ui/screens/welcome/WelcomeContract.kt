package fr.wared2003.fulcrumkiosk.ui.screens.welcome

/**
 * Represents the state of the WelcomeScreen.
 */
data class WelcomeState(
    val isLoading: Boolean = true
)

/**
 * Defines all possible user actions (events) on the WelcomeScreen.
 */
sealed interface WelcomeEvent {
    object OnConfigureClick : WelcomeEvent
}
