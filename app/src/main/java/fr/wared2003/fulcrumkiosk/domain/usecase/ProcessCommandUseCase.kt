package fr.wared2003.fulcrumkiosk.domain.usecase

import fr.wared2003.fulcrumkiosk.domain.model.Command
import fr.wared2003.fulcrumkiosk.domain.navigation.NavManager

/**
 * A use case to process a command received from a remote source (e.g., NATS).
 * This acts as a gateway to translate remote actions into application logic.
 *
 * @param navManager The navigation manager to handle navigation commands.
 */
class ProcessCommandUseCase(private val navManager: NavManager) {

    /**
     * Executes the logic for a given command.
     */
    operator fun invoke(command: Command) {
        when (command) {
            is Command.NavigateTo -> {
                // Use the NavManager to trigger a UI navigation event.
                navManager.navigate(command.screen)
            }
            // TODO: Implement other commands as needed.
            // is Command.Reboot -> { ... }
            // is Command.SetVolume -> { ... }
            else -> {
                // For now, other commands are ignored.
            }
        }
    }
}
