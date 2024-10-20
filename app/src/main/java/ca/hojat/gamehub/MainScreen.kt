package ca.hojat.gamehub

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@Preview
@Composable
fun MainScreen() {
    val navController = rememberAnimatedNavController()
    val currentScreen by navController.currentDestinationAsState()

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                currentDestination = currentScreen,
            )
        },
        content = { paddingValues ->
            AppNavigation(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
            )
        },
    )
}
