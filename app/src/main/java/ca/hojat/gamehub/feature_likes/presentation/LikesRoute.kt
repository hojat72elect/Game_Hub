@file:Suppress("MatchingDeclarationName")

package ca.hojat.gamehub.feature_likes.presentation

import ca.hojat.gamehub.common_ui.base.events.Route

/**
 * Represents the possible routes within the Likes section of the application.
 */
sealed class LikesRoute : Route {
    object Search : LikesRoute()
    data class Info(val gameId: Int) : LikesRoute()
}
