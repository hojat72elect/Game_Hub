package ca.hojat.gamehub.feature_info.presentation

import ca.hojat.gamehub.common_ui.base.events.Command
import ca.hojat.gamehub.common_ui.base.events.Route

sealed class InfoScreenCommand : Command {
    data class OpenUrl(val url: String) : InfoScreenCommand()
}

sealed class InfoScreenRoute : Route {
    data class InfoScreen(val id: Int) : InfoScreenRoute()

    data class ImageViewer(
        val gameName: String,
        val title: String?,
        val initialPosition: Int,
        val imageUrls: List<String>
    ) : InfoScreenRoute()

    object Back : InfoScreenRoute()
}
