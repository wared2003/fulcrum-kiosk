package fr.wared2003.fulcrumkiosk.ui.screens.settings

import NetworkScreen
import PlaceholderSettingsScreen
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.wared2003.fulcrumkiosk.MainActivity
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private data class SettingsMenuItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    val settingsItems = listOf(
        SettingsMenuItem("General", Icons.Default.Settings),
        SettingsMenuItem("NATS", Icons.Default.Cloud),
        SettingsMenuItem("Security", Icons.Default.Security),
        SettingsMenuItem("Network", Icons.Default.Wifi),
        SettingsMenuItem("About", Icons.Default.Info)
    )

    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val isExpandedScreen = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact

    val settingsScreens: List<@Composable () -> Unit> = listOf(
        { GeneralScreen(state, viewModel::onEvent) },
        { PlaceholderSettingsScreen("NATS") },
        { PlaceholderSettingsScreen("Security") },
        { NetworkScreen() },
        { PlaceholderSettingsScreen("About") }
    )

    val context = LocalContext.current

    // Dès que l'écran s'affiche, on libère la tablette
    LaunchedEffect(Unit) {
        (context as? MainActivity)?.unlockForAdmin()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (isExpandedScreen) {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier
                            .width(260.dp)
                            .fillMaxHeight()
                            .padding(end = 16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                            )
                            .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)),
                        drawerContainerColor = Color.Transparent,
                        drawerTonalElevation = 0.dp
                    ) {
                        NavigationDrawerContent(
                            settingsItems,
                            selectedItemIndex,
                            { selectedItemIndex = it },
                            { viewModel.onEvent(SettingsEvent.OnExitSettingsClicked) }
                        )
                    }
                },
                content = {
                    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                        ContentPane(selectedItemIndex, settingsScreens)
                    }
                }
            )
        } else {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                    ) {
                        NavigationDrawerContent(
                            settingsItems,
                            selectedItemIndex,
                            {
                                selectedItemIndex = it
                                scope.launch { drawerState.close() }
                            },
                            { viewModel.onEvent(SettingsEvent.OnExitSettingsClicked) }
                        )
                    }
                },
                content = {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(settingsItems[selectedItemIndex].label) },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            )
                        },
                        content = { padding ->
                            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                                ContentPane(selectedItemIndex, settingsScreens)
                            }
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun NavigationDrawerContent(
    settingsItems: List<SettingsMenuItem>,
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    onExitKiosk: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "SYSTEM",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 24.dp),
            letterSpacing = 2.sp
        )

        settingsItems.forEachIndexed { index, item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
                selected = index == selectedItemIndex,
                onClick = { onItemSelected(index) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(vertical = 2.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }

        Spacer(Modifier.weight(1f))

        NavigationDrawerItem(
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = MaterialTheme.colorScheme.onErrorContainer) },
            label = { Text("Back to Kiosk", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer) },
            selected = false,
            onClick = onExitKiosk,
            shape = CircleShape,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = MaterialTheme.colorScheme.errorContainer
            )
        )
    }
}

@Composable
private fun ContentPane(selectedIndex: Int, screens: List<@Composable () -> Unit>) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Crossfade(targetState = selectedIndex, label = "settings-content") {
            screens.getOrNull(it)?.invoke()
        }
    }
}



