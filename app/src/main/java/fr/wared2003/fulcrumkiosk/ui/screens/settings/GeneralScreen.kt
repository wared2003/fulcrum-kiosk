package fr.wared2003.fulcrumkiosk.ui.screens.settings

import SettingsItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralScreen(state: SettingsState, onEvent: (SettingsEvent) -> Unit) {
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
            SettingsItem(Icons.Default.Lock, "Lock Task Mode", "Status: Active", onClick = null)
        }
    }
}