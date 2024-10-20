@file:Suppress("MatchingDeclarationName")

package ca.hojat.gamehub.feature_news.presentation

import ca.hojat.gamehub.common_ui.base.events.Command
import ca.hojat.gamehub.common_ui.base.events.Route

sealed class NewsScreenCommand : Command {
    data class OpenUrl(val url: String) : NewsScreenCommand()
}

sealed class NewsScreenRoute : Route {
    data class ArticleScreen(
        val imageUrl: String?,
        val title: String,
        val lede: String,
        val publicationDate: String,
        val articleUrl: String,
        val body: String,
    ) : NewsScreenRoute()
}