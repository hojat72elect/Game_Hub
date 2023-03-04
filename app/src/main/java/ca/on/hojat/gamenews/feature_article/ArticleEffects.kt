package ca.on.hojat.gamenews.feature_article

import ca.on.hojat.gamenews.core.events.Command
import ca.on.hojat.gamenews.core.events.Route

internal sealed class ArticleCommand : Command {
    /**
     * The command for sharing the URL of this news article.
     */
    data class ShareText(val text: String) : ArticleCommand()
}


sealed class ArticleRoute : Route {
    object Back : ArticleRoute()
}