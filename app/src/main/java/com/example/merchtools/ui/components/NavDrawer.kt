package com.example.merchtools.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.merchtools.BuildConfig
import com.example.merchtools.R
import com.example.merchtools.ui.theme.MerchToolsTheme
import com.ramcosta.composedestinations.generated.destinations.HistoryScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SearchScreenDestination
import com.ramcosta.composedestinations.generated.destinations.StoreCatalogScreenDestination
import kotlinx.coroutines.launch

/**
 * Class representing a navigation drawer screen.
 *
 * @property DrawerScreens.title The title of the screen.
 * @property DrawerScreens.route The route associated with the screen.
 * @property DrawerScreens.icon The icon associated with the screen.
 * @constructor Creates a [DrawerScreens] instance.
 */
sealed class DrawerScreens(
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    object Home: DrawerScreens("Home",
        HomeScreenDestination.route,
        Icons.Default.Home
    )
    object Search: DrawerScreens("SKU Catalog",
        SearchScreenDestination.route,
        Icons.Default.Search
    )
    object Stores: DrawerScreens("Store Catalog",
        StoreCatalogScreenDestination.route,
        Icons.Default.Store
    )
    object History: DrawerScreens("Audit History",
        HistoryScreenDestination.route,
        Icons.Default.History
    )
}

// Create a list of screens to iterate over, determining their routes and icons
// and if selected = true
private val screens = listOf(
    DrawerScreens.Home,
    DrawerScreens.Search,
    DrawerScreens.Stores,
    DrawerScreens.History
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedNavDrawer(
    title: String,
    currentRoute: String?,
    onDestinationClicked: (route: String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
    ) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val appVersion = BuildConfig.VERSION_NAME

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                drawerShape = RectangleShape
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                        .fillMaxHeight()
                ) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Merch Tools Menu",
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.padding_small))
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 4.dp),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    screens.forEach { screen ->
                        val selected = screen.route == currentRoute
                        NavigationDrawerItem(
                            label = { Text(screen.title) },
                            selected = selected,
                            icon = { Icon(screen.icon, contentDescription = null) },
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                if (!selected) {
                                    onDestinationClicked(screen.route)
                                    println("DEBUG: Selected: ${screen.route}")
                                }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedTextColor = MaterialTheme.colorScheme.onSecondary,
                                selectedIconColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )

                        Spacer(Modifier.padding(vertical = 4.dp))
                    }
                }

                Spacer(Modifier.weight(1f))
                Text(
                    text = "v$appVersion 💚",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(
                        title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    ) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            onDestinationClicked(DrawerScreens.Home.route)
                        },
                            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_medium))
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceDim,
                        titleContentColor = MaterialTheme.colorScheme.secondary,
                        navigationIconContentColor = MaterialTheme.colorScheme.secondary,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailedNavDrawerPreview() {
    MerchToolsTheme {
        DetailedNavDrawer(
            title = "Home",
            currentRoute = null,
            onDestinationClicked = {},
            content = {}
        )
    }
}