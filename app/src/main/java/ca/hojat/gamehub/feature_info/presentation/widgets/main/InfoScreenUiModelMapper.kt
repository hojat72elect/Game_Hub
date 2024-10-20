package ca.hojat.gamehub.feature_info.presentation.widgets.main

import ca.hojat.gamehub.core.domain.entities.Game
import ca.hojat.gamehub.feature_info.domain.entities.InfoScreenData
import ca.hojat.gamehub.feature_info.presentation.widgets.companies.InfoScreenCompanyUiModelMapper
import ca.hojat.gamehub.feature_info.presentation.widgets.details.InfoScreenDetailsUiModelMapper
import ca.hojat.gamehub.feature_info.presentation.widgets.header.InfoScreenHeaderUiModelMapper
import ca.hojat.gamehub.feature_info.presentation.widgets.links.InfoScreenLinkUiModelMapper
import ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames.RelatedGamesUiModel
import ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames.mappers.InfoScreenOtherCompanyGamesUiModelMapper
import ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames.mappers.InfoScreenSimilarGamesUiModelMapper
import ca.hojat.gamehub.feature_info.presentation.widgets.screenshots.InfoScreenShotUiModelMapper
import ca.hojat.gamehub.feature_info.presentation.widgets.videos.InfoScreenVideoUiModelMapper
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface InfoScreenUiModelMapper {

    fun mapToUiModel(infoScreenData: InfoScreenData): InfoScreenUiModel
}

@BindType(installIn = BindType.Component.VIEW_MODEL)
@Suppress("LongParameterList")
class InfoScreenUiModelMapperImpl @Inject constructor(
    private val headerModelMapper: InfoScreenHeaderUiModelMapper,
    private val videoModelMapper: InfoScreenVideoUiModelMapper,
    private val screenshotModelMapper: InfoScreenShotUiModelMapper,
    private val detailsModelMapper: InfoScreenDetailsUiModelMapper,
    private val linkModelMapper: InfoScreenLinkUiModelMapper,
    private val companyModelMapper: InfoScreenCompanyUiModelMapper,
    private val otherCompanyGamesModelMapper: InfoScreenOtherCompanyGamesUiModelMapper,
    private val similarGamesModelMapper: InfoScreenSimilarGamesUiModelMapper,
) : InfoScreenUiModelMapper {

    override fun mapToUiModel(infoScreenData: InfoScreenData): InfoScreenUiModel {

        return InfoScreenUiModel(
            id = infoScreenData.game.id,
            headerModel = headerModelMapper.mapToUiModel(
                infoScreenData.game,
                infoScreenData.isGameLiked
            ),
            videoModels = videoModelMapper.mapToUiModels(infoScreenData.game.videos),
            screenshotModels = screenshotModelMapper.mapToUiModels(infoScreenData.game.screenshots),
            summary = infoScreenData.game.summary,
            detailsModel = detailsModelMapper.mapToUiModel(infoScreenData.game),
            linkModels = linkModelMapper.mapToUiModels(infoScreenData.game.websites),
            companyModels = companyModelMapper.mapToUiModels(infoScreenData.game.involvedCompanies),
            otherCompanyGames = infoScreenData.game.createOtherCompanyGamesUiModel(infoScreenData.companyGames),
            similarGames = similarGamesModelMapper.mapToUiModel(infoScreenData.similarGames),
        )
    }

    private fun Game.createOtherCompanyGamesUiModel(otherCompanyGames: List<Game>): RelatedGamesUiModel? {
        return otherCompanyGamesModelMapper.mapToUiModel(otherCompanyGames, this)
    }
}
