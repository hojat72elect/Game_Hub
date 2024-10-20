package ca.hojat.gamehub.feature_article

import ca.hojat.gamehub.common_ui.base.events.Command
import ca.hojat.gamehub.common_ui.base.events.Route

sealed class ArticleCommand : Command {
    /**
     * The command for sharing the URL of this news article.
     */
    data class ShareText(val text: String) : ArticleCommand()
}


sealed class ArticleRoute : Route {
    object Back : ArticleRoute()
}