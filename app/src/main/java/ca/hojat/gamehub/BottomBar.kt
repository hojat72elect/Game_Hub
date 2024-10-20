package ca.hojat.gamehub

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import ca.hojat.gamehub.common_ui.theme.GameHubTheme

private const val BOTTOM_BAR_ANIMATION_DURATION = 300

@Composable
fun BottomBar(
    navController: NavHostController,
    currentDestination: Destination,
) {
    val isBottomBarScreenDisplayed = remember(currentDestination) {
        BottomNavigationItemModel
            .values()
            .any { itemModel -> itemModel.destination == currentDestination }
    }

    LaunchedEffect(isBottomBarScreenDisplayed) {
        navController.enableOnBackPressed(!isBottomBarScreenDisplayed)
    }

    AnimatedVisibility(
        visible = isBottomBarScreenDisplayed,
        enter = slideInVertically(
            animationSpec = tween(BOTTOM_BAR_ANIMATION_DURATION),
            initialOffsetY = { it },
        ),
        exit = slideOutVertically(
            animationSpec = tween(BOTTOM_BAR_ANIMATION_DURATION),
            targetOffsetY = { it },
        ),
    ) {
        BottomBarNavigation(
            navController = navController,
            currentDestination = currentDestination,
        )
    }
}

@Composable
private fun BottomBarNavigation(
    navController: NavHostController,
    currentDestination: Destination,
) {
    BottomNavigation(
        modifier = Modifier.navigationBarsPadding(),
        backgroundColor = GameHubTheme.colors.primary,
    ) {
        for (bottomNavigationItemModel in BottomNavigationItemModel.values()) {
            val itemScreen = bottomNavigationItemModel.destination

            BottomNavigationItem(
                selected = currentDestination == itemScreen,
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(itemScreen.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(bottomNavigationItemModel.iconId),
                        contentDescription = null,
                    )
                },
                label = {
                    Text(text = stringResource(bottomNavigationItemModel.titleId), maxLines = 1)
                },
                selectedContentColor = GameHubTheme.colors.secondary,
                unselectedContentColor = GameHubTheme.colors.onBackground,
            )
        }
    }
}

private enum class BottomNavigationItemModel(
    @DrawableRes
    val iconId: Int,
    @StringRes
    val titleId: Int,
    val destination: Destination,
) {
    DISCOVER(
        iconId = R.drawable.compass_rose,
        titleId = R.string.games_discovery_toolbar_title,
        destination = Destination.Discover,
    ),
    LIKES(
        iconId = R.drawable.heart,
        titleId = R.string.liked_games_toolbar_title,
        destination = Destination.Likes,
    ),
    NEWS(
        iconId = R.drawable.newspaper,
        titleId = R.string.gaming_news_toolbar_title,
        destination = Destination.News,
    ),
    SETTINGS(
        iconId = R.drawable.cog_outline,
        titleId = R.string.settings_toolbar_title,
        destination = Destination.Settings,
    ),
}
