package ca.hojat.gamehub

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ca.hojat.gamehub.common_ui.HorizontalSliding
import ca.hojat.gamehub.common_ui.OvershootScaling
import ca.hojat.gamehub.feature_article.ArticleRoute
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import ca.hojat.gamehub.feature_category.CategoryScreenRoute
import ca.hojat.gamehub.feature_category.widgets.CategoryScreen
import ca.hojat.gamehub.feature_discovery.DiscoverScreenRoute
import ca.hojat.gamehub.feature_discovery.widgets.DiscoverScreen
import ca.hojat.gamehub.feature_news.presentation.widgets.NewsScreen
import ca.hojat.gamehub.feature_settings.presentation.Settings
import ca.hojat.gamehub.feature_image_viewer.ImageViewer
import ca.hojat.gamehub.feature_image_viewer.ImageViewerRoute
import ca.hojat.gamehub.feature_info.presentation.InfoScreenRoute
import ca.hojat.gamehub.feature_info.presentation.widgets.main.InfoScreen
import ca.hojat.gamehub.feature_likes.presentation.LikesScreen
import ca.hojat.gamehub.feature_likes.presentation.LikesRoute
import ca.hojat.gamehub.feature_news.presentation.NewsScreenRoute
import ca.hojat.gamehub.feature_article.ArticleScreen
import ca.hojat.gamehub.feature_search.presentation.GamesSearch
import ca.hojat.gamehub.feature_search.presentation.GamesSearchRoute

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier,
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = START_DESTINATION.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        discoverScreen(
            navController = navController,
            modifier = modifier,
        )
        likesScreen(
            navController = navController,
            modifier = modifier,
        )
        newsScreen(
            navController = navController,
            modifier = modifier
        )
        settingsScreen(modifier = modifier)
        gamesSearchScreen(navController = navController)
        gamesCategoryScreen(navController = navController)
        gameInfoScreen(navController = navController)
        imageViewerScreen(navController = navController)
        articleScreen(navController = navController)
    }
}

/**
 * Main screen of the app, you could consider it as home screen.
 */
private fun NavGraphBuilder.discoverScreen(
    navController: NavHostController,
    modifier: Modifier,
) {
    composable(
        route = Destination.Discover.route,
        enterTransition = { null },
        exitTransition = {
            when (Destination.forRoute(targetState.destination.requireRoute())) {
                Destination.Search -> OvershootScaling.exit()
                Destination.Category,
                Destination.InfoPage -> HorizontalSliding.exit()
                else -> null
            }
        },
        popEnterTransition = {
            when (Destination.forRoute(initialState.destination.requireRoute())) {
                Destination.Search -> OvershootScaling.popEnter()
                Destination.Category,
                Destination.InfoPage -> HorizontalSliding.popEnter()
                else -> null
            }
        },
        popExitTransition = { null },
    ) {
        DiscoverScreen(modifier) { route ->
            when (route) {
                is DiscoverScreenRoute.Search -> {
                    navController.navigate(Destination.Search.route)
                }
                is DiscoverScreenRoute.Category -> {
                    navController.navigate(Destination.Category.createLink(route.category))
                }
                is DiscoverScreenRoute.Info -> {
                    navController.navigate(Destination.InfoPage.createLink(route.itemId))
                }
            }
        }
    }
}

/**
 * The screen for viewing all the games that user has already liked.
 */
private fun NavGraphBuilder.likesScreen(
    navController: NavHostController,
    modifier: Modifier,
) {
    composable(
        route = Destination.Likes.route,
        enterTransition = { null },
        exitTransition = {
            when (Destination.forRoute(targetState.destination.requireRoute())) {
                Destination.Search -> OvershootScaling.exit()
                Destination.InfoPage -> HorizontalSliding.exit()
                else -> null
            }
        },
        popEnterTransition = {
            when (Destination.forRoute(initialState.destination.requireRoute())) {
                Destination.Search -> OvershootScaling.popEnter()
                Destination.InfoPage -> HorizontalSliding.popEnter()
                else -> null
            }
        },
        popExitTransition = { null },
    ) {
        LikesScreen(modifier) { route ->
            when (route) {
                is LikesRoute.Search -> {
                    navController.navigate(Destination.Search.route)
                }
                is LikesRoute.Info -> {
                    navController.navigate(Destination.InfoPage.createLink(route.gameId))
                }
            }
        }
    }
}

/**
 * The screen for browsing through a list of news articles.
 */
private fun NavGraphBuilder.newsScreen(
    navController: NavHostController,
    modifier: Modifier
) {
    composable(
        route = Destination.News.route,
    ) {
        NewsScreen(modifier) { route ->
            when (route) {
                is NewsScreenRoute.ArticleScreen -> {
                    navController.navigate(
                        Destination.Article.createLink(
                            imageUrl = route.imageUrl,
                            title = route.title,
                            lede = route.lede,
                            publicationDate = route.publicationDate,
                            articleUrl = route.articleUrl,
                            body = route.body,
                        )
                    )
                }
            }
        }
    }
}

/**
 * The screen for reading a specific article.
 */
private fun NavGraphBuilder.articleScreen(navController: NavHostController) {
    composable(
        route = Destination.Article.route,
        arguments = listOf(
            navArgument(Destination.Article.Parameters.IMAGE_URL) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(Destination.Article.Parameters.TITLE) {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument(Destination.Article.Parameters.LEDE) {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument(Destination.Article.Parameters.PUBLICATION_DATE) {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument(Destination.Article.Parameters.ARTICLE_URL) {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument(Destination.Article.Parameters.BODY) {
                type = NavType.StringType
                defaultValue = ""
            },
        ),
    ) {
        ArticleScreen { route ->
            when (route) {
                is ArticleRoute.Back -> navController.popBackStack()
            }
        }
    }
}

/**
 * The screen for viewing and changing all the app-level settings.
 */
private fun NavGraphBuilder.settingsScreen(modifier: Modifier) {
    composable(
        route = Destination.Settings.route,
    ) {
        Settings(modifier)
    }
}

/**
 * The screen for searching desired game (by entering its name).
 */
private fun NavGraphBuilder.gamesSearchScreen(navController: NavHostController) {
    composable(
        route = Destination.Search.route,
        enterTransition = {
            when (Destination.forRoute(initialState.destination.requireRoute())) {
                Destination.News,
                Destination.Discover,
                Destination.Likes -> OvershootScaling.enter()
                else -> null
            }
        },
        exitTransition = {
            when (Destination.forRoute(targetState.destination.requireRoute())) {
                Destination.InfoPage -> HorizontalSliding.exit()
                else -> null
            }
        },
        popEnterTransition = {
            when (Destination.forRoute(initialState.destination.requireRoute())) {
                Destination.InfoPage -> HorizontalSliding.popEnter()
                else -> null
            }
        },
        popExitTransition = {
            when (Destination.forRoute(targetState.destination.requireRoute())) {
                Destination.News,
                Destination.Discover,
                Destination.Likes -> OvershootScaling.popExit()
                else -> null
            }
        },
    ) {
        GamesSearch { route ->
            when (route) {
                is GamesSearchRoute.Info -> {
                    navController.navigate(Destination.InfoPage.createLink(route.gameId))
                }
                is GamesSearchRoute.Back -> {
                    navController.popBackStack()
                }
            }
        }
    }
}

/**
 * The screen for showing the games in any of the categories that are depicted in [DiscoverScreen].
 */
private fun NavGraphBuilder.gamesCategoryScreen(navController: NavHostController) {
    composable(
        route = Destination.Category.route,
        arguments = listOf(
            navArgument(Destination.Category.Parameters.CATEGORY) { type = NavType.StringType },
        ),
        enterTransition = {
            when (Destination.forRoute(initialState.destination.requireRoute())) {
                Destination.Discover -> HorizontalSliding.enter()
                else -> null
            }
        },
        exitTransition = {
            when (Destination.forRoute(targetState.destination.requireRoute())) {
                Destination.InfoPage -> HorizontalSliding.exit()
                else -> null
            }
        },
        popEnterTransition = {
            when (Destination.forRoute(initialState.destination.requireRoute())) {
                Destination.InfoPage -> HorizontalSliding.popEnter()
                else -> null
            }
        },
        popExitTransition = {
            when (Destination.forRoute(targetState.destination.requireRoute())) {
                Destination.Discover -> HorizontalSliding.popExit()
                else -> null
            }
        },
    ) {
        CategoryScreen { route ->
            when (route) {
                is CategoryScreenRoute.Info -> {
                    navController.navigate(Destination.InfoPage.createLink(route.gameId))
                }
                is CategoryScreenRoute.Back -> {
                    navController.popBackStack()
                }
            }
        }
    }
}

/**
 * The screen for viewing info about a specific game.
 */
private fun NavGraphBuilder.gameInfoScreen(navController: NavHostController) {
    composable(
        route = Destination.InfoPage.route,
        arguments = listOf(
            navArgument(Destination.InfoPage.Parameters.GAME_ID) { type = NavType.IntType },
        ),
        enterTransition = {
            when (Destination.forRoute(initialState.destination.requireRoute())) {
                Destination.Discover,
                Destination.Likes,
                Destination.Search,
                Destination.Category,
                Destination.InfoPage -> HorizontalSliding.enter()
                else -> null
            }
        },
        exitTransition = {
            when (Destination.forRoute(targetState.destination.requireRoute())) {
                Destination.ImageViewer,
                Destination.InfoPage -> HorizontalSliding.exit()
                else -> null
            }
        },
        popEnterTransition = {
            when (Destination.forRoute(initialState.destination.requireRoute())) {
                Destination.ImageViewer,
                Destination.InfoPage -> HorizontalSliding.popEnter()
                else -> null
            }
        },
        popExitTransition = {
            when (Destination.forRoute(targetState.destination.requireRoute())) {
                Destination.Discover,
                Destination.Likes,
                Destination.Search,
                Destination.Category,
                Destination.InfoPage -> HorizontalSliding.popExit()
                else -> null
            }
        },
    ) {
        InfoScreen { route ->
            when (route) {
                is InfoScreenRoute.ImageViewer -> {
                    navController.navigate(
                        Destination.ImageViewer.createLink(
                            gameName = route.gameName,
                            title = route.title,
                            initialPosition = route.initialPosition,
                            imageUrls = route.imageUrls,
                        )
                    )
                }
                is InfoScreenRoute.InfoScreen -> {
                    navController.navigate(Destination.InfoPage.createLink(route.id))
                }
                is InfoScreenRoute.Back -> {
                    navController.popBackStack()
                }
            }
        }
    }
}

/**
 * The screen for viewing images of a game.
 */
private fun NavGraphBuilder.imageViewerScreen(navController: NavHostController) {
    composable(
        route = Destination.ImageViewer.route,
        arguments = listOf(
            navArgument(Destination.ImageViewer.Parameters.GAME_NAME) {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument(Destination.ImageViewer.Parameters.TITLE) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(Destination.ImageViewer.Parameters.INITIAL_POSITION) {
                type = NavType.IntType
                defaultValue = 0
            },
            navArgument(Destination.ImageViewer.Parameters.IMAGE_URLS) {
                type = NavType.StringType
                nullable = true
            }
        ),
        enterTransition = {
            when (Destination.forRoute(initialState.destination.requireRoute())) {
                Destination.InfoPage -> HorizontalSliding.enter()
                else -> null
            }
        },
        exitTransition = { null },
        popEnterTransition = { null },
        popExitTransition = {
            when (Destination.forRoute(targetState.destination.requireRoute())) {
                Destination.InfoPage -> HorizontalSliding.popExit()
                else -> null
            }
        },
    ) {
        ImageViewer { route ->
            when (route) {
                is ImageViewerRoute.Back -> navController.popBackStack()
            }
        }
    }
}
