package ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames.mappers

import ca.hojat.gamehub.R
import ca.hojat.gamehub.core.providers.StringProvider
import ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames.RelatedGameUiModel
import ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames.RelatedGamesType
import ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames.RelatedGamesUiModel
import ca.hojat.gamehub.core.factories.IgdbImageSize
import ca.hojat.gamehub.core.factories.IgdbImageUrlFactory
import ca.hojat.gamehub.core.domain.entities.Game
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface InfoScreenOtherCompanyGamesUiModelMapper {

    fun mapToUiModel(
        companyGames: List<Game>,
        currentGame: Game,
    ): RelatedGamesUiModel?
}

@BindType(installIn = BindType.Component.VIEW_MODEL)
class InfoScreenOtherCompanyGamesUiModelMapperImpl @Inject constructor(
    private val stringProvider: StringProvider,
    private val igdbImageUrlFactory: IgdbImageUrlFactory,
) : InfoScreenOtherCompanyGamesUiModelMapper {

    override fun mapToUiModel(
        companyGames: List<Game>,
        currentGame: Game
    ): RelatedGamesUiModel? {
        return companyGames
            .filter { it.id != currentGame.id }
            .takeIf(List<Game>::isNotEmpty)
            ?.let { games ->
                RelatedGamesUiModel(
                    type = RelatedGamesType.OTHER_COMPANY_GAMES,
                    title = currentGame.createOtherCompanyGamesModelTitle(),
                    items = games.toRelatedGameUiModels()
                )
            }
    }

    private fun Game.createOtherCompanyGamesModelTitle(): String {
        val developerName = developerCompany?.name
            ?: stringProvider.getString(R.string.game_info_other_company_games_title_default_arg)

        return stringProvider.getString(
            R.string.game_info_other_company_games_title_template,
            developerName
        )
    }

    private fun List<Game>.toRelatedGameUiModels(): List<RelatedGameUiModel> {
        return map {
            RelatedGameUiModel(
                id = it.id,
                title = it.name,
                coverUrl = it.cover?.let { cover ->
                    igdbImageUrlFactory.createUrl(
                        cover,
                        IgdbImageUrlFactory.Config(IgdbImageSize.BIG_COVER)
                    )
                }
            )
        }
    }
}
