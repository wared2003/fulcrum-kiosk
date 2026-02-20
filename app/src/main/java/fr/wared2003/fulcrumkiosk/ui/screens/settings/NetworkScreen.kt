import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NetworkScreen() {
    var activeSubMenu by remember { mutableStateOf<String?>(null) }

    Crossfade(targetState = activeSubMenu, label = "network-nav") { screen ->
        when (screen) {
            "wifi" -> {
                SubMenuLayout(title = "Wi-Fi Settings", onBack = { activeSubMenu = null }) {
                    PlaceholderSettingsScreen("Wi-Fi Scanner")
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
                            icon = Icons.Default.Wifi,
                            title = "Wi-Fi",
                            subtitle = "Connect to a wireless network",
                            onClick = { activeSubMenu = "wifi" }
                        )
                    }
                }
            }
        }
    }
}
