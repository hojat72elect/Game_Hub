package ca.hojat.gamehub.feature_info.presentation.widgets.details

import ca.hojat.gamehub.core.domain.entities.Game
import ca.hojat.gamehub.core.domain.entities.Genre
import ca.hojat.gamehub.core.domain.entities.Mode
import ca.hojat.gamehub.core.domain.entities.Platform
import ca.hojat.gamehub.core.domain.entities.PlayerPerspective
import ca.hojat.gamehub.core.domain.entities.Theme
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface InfoScreenDetailsUiModelMapper {
    fun mapToUiModel(game: Game): InfoScreenDetailsUiModel?
}

@BindType(installIn = BindType.Component.VIEW_MODEL)
class InfoScreenDetailsUiModelMapperImpl @Inject constructor() :
    InfoScreenDetailsUiModelMapper {

    private companion object {
        private const val TEXT_SEPARATOR = " • "
    }

    override fun mapToUiModel(game: Game): InfoScreenDetailsUiModel? {
        @Suppress("ComplexCondition")
        if (game.genres.isEmpty() &&
            game.platforms.isEmpty() &&
            game.modes.isEmpty() &&
            game.playerPerspectives.isEmpty() &&
            game.themes.isEmpty()
        ) {
            return null
        }

        return InfoScreenDetailsUiModel(
            genresText = game.genresToText(),
            platformsText = game.platformsToText(),
            modesText = game.modesToText(),
            playerPerspectivesText = game.playerPerspectivesToText(),
            themesText = game.themesToText()
        )
    }

    private fun Game.genresToText(): String? {
        return genres
            .takeIf(List<Genre>::isNotEmpty)
            ?.map(Genre::name)
            ?.joinToString()
    }

    private fun Game.platformsToText(): String? {
        return platforms
            .takeIf(List<Platform>::isNotEmpty)
            ?.map(Platform::name)
            ?.joinToString()
    }

    private fun Game.modesToText(): String? {
        return modes
            .takeIf(List<Mode>::isNotEmpty)
            ?.map(Mode::name)
            ?.joinToString()
    }

    private fun Game.playerPerspectivesToText(): String? {
        return playerPerspectives
            .takeIf(List<PlayerPerspective>::isNotEmpty)
            ?.map(PlayerPerspective::name)
            ?.joinToString()
    }

    private fun Game.themesToText(): String? {
        return themes
            .takeIf(List<Theme>::isNotEmpty)
            ?.map(Theme::name)
            ?.joinToString()
    }

    private fun List<String>.joinToString(): String {
        return joinToString(separator = TEXT_SEPARATOR)
    }
}
