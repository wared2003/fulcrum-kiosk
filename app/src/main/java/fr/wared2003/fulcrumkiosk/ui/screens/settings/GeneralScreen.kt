package fr.wared2003.fulcrumkiosk.ui.screens.settings

import SettingsItem
import SubMenuLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralScreen(state: SettingsState, onEvent: (SettingsEvent) -> Unit) {
    var activeSubMenu by remember { mutableStateOf<String?>(null) }

    if (state.showUrlDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(SettingsEvent.OnDismissUrlDialog) },
            title = { Text("Update Kiosk URL") },
            text = {
                OutlinedTextField(
                    value = state.url,
                    onValueChange = { onEvent(SettingsEvent.OnUrlChanged(it)) },
                    label = { Text("Target URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { onEvent(SettingsEvent.OnSaveUrlClicked) }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { onEvent(SettingsEvent.OnDismissUrlDialog) }) { Text("Cancel") } }
        )
    }

    Crossfade(targetState = activeSubMenu, label = "general-nav") { screen ->
        when (screen) {
            "brightness" -> {
                SubMenuLayout(title = "Brightness Settings", onBack = { activeSubMenu = null }) {
                    BrightnessScreen(state, onEvent)
                }
            }
            "power" -> {
                SubMenuLayout(title = "Power Saving Settings", onBack = { activeSubMenu = null }) {
                    PowerSavingScreen(state, onEvent)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SettingsItem(
                            icon = Icons.Default.Language,
                            title = "PWA URL",
                            subtitle = state.url.ifEmpty { "Enter server address" },
                            onClick = { onEvent(SettingsEvent.OnPwaUrlClicked) }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.SystemUpdate,
                            title = "Launch on Boot",
                            subtitle = if (state.launchOnBoot) "Enabled" else "Disabled",
                            trailingContent = {
                                Switch(
                                    checked = state.launchOnBoot,
                                    onCheckedChange = { onEvent(SettingsEvent.OnLaunchOnBootChanged(it)) }
                                )
                            }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Brightness7,
                            title = "Brightness",
                            subtitle = "Adjust screen brightness",
                            onClick = { activeSubMenu = "brightness" }
                        )
                    }
                    item {
                        SettingsItem(
                            icon = Icons.Default.Power,
                            title = "Power Saving",
                            subtitle = "Configure power saving settings",
                            onClick = { activeSubMenu = "power" }
                        )
                    }
                }
            }
        }
    }
}