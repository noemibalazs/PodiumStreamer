import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import podiumstreamer.composeapp.generated.resources.Res
import podiumstreamer.composeapp.generated.resources.label_icon_content_description
import podiumstreamer.composeapp.generated.resources.label_podium_streamer
import podiumstreamer.composeapp.generated.resources.logo
import moe.tlaster.precompose.PreComposeApp
import navigation.PodiumDestination
import org.jetbrains.compose.resources.stringResource
import screens.FavoriteScreen
import screens.StreamerScreen
import theme.PhilosopherFontFamily
import theme.StreamerTheme

@Composable
@Preview
fun App() {

    val snackbarHostState = remember { SnackbarHostState() }
    val modifier: Modifier = Modifier
    val navController = rememberNavController()

    StreamerTheme {

        PreComposeApp {

            Scaffold(
                topBar = {
                    StreamerAppBar(
                        title = stringResource(Res.string.label_podium_streamer),
                        contentDescription = stringResource(Res.string.label_icon_content_description),
                        modifier = modifier
                    )
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                content = {
                    Column {
                        StreamerNavigationHost(navHostController = navController, snackBarHostState = snackbarHostState, modifier = modifier)
                    }
                },
                bottomBar = {
                    StreamerBottomNavigationBar(
                        navController = navController,
                        modifier = modifier
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StreamerAppBar(title: String, contentDescription: String, modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = PhilosopherFontFamily(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = contentDescription,
                modifier = modifier.size(32.dp).padding(start = 12.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun StreamerNavigationHost(navHostController: NavHostController, snackBarHostState: SnackbarHostState, modifier: Modifier = Modifier) {
    NavHost(navController = navHostController, startDestination = PodiumDestination.STREAMS.name) {
        composable(PodiumDestination.STREAMS.name) {
            StreamerScreen(snackBarHostState = snackBarHostState, modifier = modifier)
        }

        composable(PodiumDestination.FAVORITES.name) {
            FavoriteScreen(snackBarHostState = snackBarHostState, modifier = modifier)
        }
    }
}

@Composable
private fun StreamerBottomNavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {
    val destinations = PodiumDestination.getDestinations()

    NavigationBar {

        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        destinations.forEach { destination ->

            NavigationBarItem(
                label = {
                    Text(
                        text = stringResource(destination.titleResId),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(destination.iconResId),
                        modifier = modifier.size(24.dp),
                        contentDescription = null
                    )
                },
                selected = currentRoute == destination.name,
                onClick = {

                    navController.navigate(destination.name) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
