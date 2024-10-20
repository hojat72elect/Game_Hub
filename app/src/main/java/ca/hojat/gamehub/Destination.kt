package ca.hojat.gamehub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import ca.hojat.gamehub.core.extensions.toCsv
import java.net.URLEncoder

val START_DESTINATION = Destination.Discover

sealed class Destination(val route: String) {
    object Discover : Destination("discover")
    object Likes : Destination("likes")
    object News : Destination("news")
    object Settings : Destination("settings")
    object Search : Destination("search")

    /**
     * The page that user will head to if they click on "SEE ALL" section
     * of each of categories in "Discover" page.
     */
    object Category : Destination("discover/{${Parameters.CATEGORY}}") {
        object Parameters {
            const val CATEGORY = "category"
        }

        fun createLink(category: String): String {
            return "discover/$category"
        }
    }

    /**
     * The page that user will end up in when they click on each
     * one of the items in "Likes" or "Discover" pages.
     */
    object InfoPage : Destination("info-page/{${Parameters.GAME_ID}}") {
        object Parameters {
            const val GAME_ID = "game-id"
        }

        fun createLink(gameId: Int): String {
            return "info-page/$gameId"
        }
    }

    /**
     * if in the [InfoPage] you click on any images, you will go here.
     */
    object ImageViewer : Destination(
        "image-viewer?" +
                "${Parameters.GAME_NAME}={${Parameters.GAME_NAME}}&" +
                "${Parameters.TITLE}={${Parameters.TITLE}}&" +
                "${Parameters.INITIAL_POSITION}={${Parameters.INITIAL_POSITION}}&" +
                "${Parameters.IMAGE_URLS}={${Parameters.IMAGE_URLS}}"
    ) {
        /**
         * Any of the parameters that you will take from [InfoPage] to [ImageViewer] with yourself.
         */
        object Parameters {
            const val GAME_NAME = "game-name"
            const val TITLE = "title"
            const val INITIAL_POSITION = "initial-position"
            const val IMAGE_URLS = "image-urls"
        }

        fun createLink(
            gameName: String,
            title: String?,
            initialPosition: Int,
            imageUrls: List<String>,
        ): String {
            val modifiedImageUrls = imageUrls
                .map { imageUrl -> URLEncoder.encode(imageUrl, "UTF-8") }
                .toCsv()

            return buildString {
                append("image-viewer?")

                append("${Parameters.GAME_NAME}=$gameName&")

                if (title != null) {
                    append("${Parameters.TITLE}=$title&")
                }

                append("${Parameters.INITIAL_POSITION}=$initialPosition&")
                append("${Parameters.IMAGE_URLS}=$modifiedImageUrls")
            }
        }
    }

    /**
     * The screen for viewing a specific news item.
     */
    object Article : Destination(
        "article?" +
                "${Parameters.IMAGE_URL}={${Parameters.IMAGE_URL}}&" +
                "${Parameters.TITLE}={${Parameters.TITLE}}&" +
                "${Parameters.LEDE}={${Parameters.LEDE}}&" +
                "${Parameters.PUBLICATION_DATE}={${Parameters.PUBLICATION_DATE}}&" +
                "${Parameters.ARTICLE_URL}={${Parameters.ARTICLE_URL}}&" +
                "${Parameters.BODY}={${Parameters.BODY}}"
    ) {

        /**
         * Any of the parameters that you will take from [News] to [Article] with yourself.
         */
        object Parameters {
            const val IMAGE_URL = "image-url"
            const val TITLE = "title"
            const val LEDE = "lede"
            const val PUBLICATION_DATE = "publication-date"
            const val ARTICLE_URL = "article-url"
            const val BODY = "body"
        }

        fun createLink(
            imageUrl: String?,
            title: String,
            lede: String,
            publicationDate: String,
            articleUrl: String,
            body: String
        ): String {
            return buildString {
                append("article?")

                if (imageUrl != null) {
                    append("${Parameters.IMAGE_URL}=$imageUrl&")
                }
                append("${Parameters.TITLE}=$title&")
                append("${Parameters.LEDE}=$lede&")
                append("${Parameters.PUBLICATION_DATE}=$publicationDate&")
                append("${Parameters.ARTICLE_URL}=$articleUrl&")
                append("${Parameters.BODY}=$body")
            }
        }

    }

    companion object {

        val Saver = Saver(
            save = { it.route },
            restore = Companion::forRoute,
        )

        fun forRoute(route: String): Destination {
            return when (route) {
                Discover.route -> Discover
                Likes.route -> Likes
                News.route -> News
                Settings.route -> Settings
                Search.route -> Search
                Category.route -> Category
                InfoPage.route -> InfoPage
                ImageViewer.route -> ImageViewer
                Article.route -> Article
                else -> error("Cannot find screen for the route: $route.")
            }
        }
    }
}

@Stable
@Composable
fun NavController.currentDestinationAsState(): State<Destination> {
    val selectedDestination = rememberSaveable(stateSaver = Destination.Saver) {
        mutableStateOf(START_DESTINATION)
    }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedDestination.value =
                Destination.forRoute(checkNotNull(destination.requireRoute()))
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedDestination
}
