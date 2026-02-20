package fr.wared2003.fulcrumkiosk.ui.screens.settings

import SettingsItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BrightnessScreen(state: SettingsState, onEvent: (SettingsEvent) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(PaddingValues(24.dp)),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SettingsItem(
                icon = Icons.Default.BrightnessAuto,
                title = "Auto-Brightness",
                subtitle = if (state.isAutoBrightness) "Enabled" else "Disabled",
                trailingContent = {
                    Switch(
                        checked = state.isAutoBrightness,
                        onCheckedChange = { onEvent(SettingsEvent.OnAutoBrightnessChanged(it)) }
                    )
                }
            )
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Brightness", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("Manually adjust brightness") },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Brightness7, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    )
                    Slider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        value = state.brightness,
                        onValueChange = { onEvent(SettingsEvent.OnBrightnessChanged(it)) },
                        valueRange = 0f..1f,
                        enabled = !state.isAutoBrightness
                    )
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Min Auto-Brightness", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("Minimum brightness for auto-mode") },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.BrightnessLow, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    )
                    Slider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        value = state.autoBrightnessMin,
                        onValueChange = { onEvent(SettingsEvent.OnAutoBrightnessMinChanged(it)) },
                        valueRange = 0f..1f,
                        enabled = state.isAutoBrightness
                    )
                }
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Max Auto-Brightness", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("Maximum brightness for auto-mode") },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.BrightnessHigh, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    )
                    Slider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        value = state.autoBrightnessMax,
                        onValueChange = { onEvent(SettingsEvent.OnAutoBrightnessMaxChanged(it)) },
                        valueRange = 0f..1f,
                        enabled = state.isAutoBrightness
                    )
                }
            }
        }
    }
}