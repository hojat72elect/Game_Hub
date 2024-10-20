package ca.hojat.gamehub.feature_info.presentation.widgets.header

import ca.hojat.gamehub.core.domain.entities.Game
import ca.hojat.gamehub.core.factories.IgdbImageSize
import ca.hojat.gamehub.core.factories.IgdbImageUrlFactory
import ca.hojat.gamehub.core.formatters.GameAgeRatingFormatter
import ca.hojat.gamehub.core.formatters.GameCategoryFormatter
import ca.hojat.gamehub.core.formatters.GameRatingFormatter
import ca.hojat.gamehub.core.formatters.GameReleaseDateFormatter
import ca.hojat.gamehub.feature_info.domain.LikeCountCalculator
import ca.hojat.gamehub.feature_info.presentation.widgets.header.artworks.InfoScreenArtworkUiModelMapper
import ca.hojat.gamehub.feature_info.presentation.widgets.header.artworks.InfoScreenArtworkUiModel
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

interface InfoScreenHeaderUiModelMapper {
    fun mapToUiModel(game: Game, isLiked: Boolean): InfoScreenHeaderUiModel
}

@BindType(installIn = BindType.Component.VIEW_MODEL)
class InfoScreenHeaderUiModelMapperImpl @Inject constructor(
    private val igdbImageUrlFactory: IgdbImageUrlFactory,
    private val artworkModelMapper: InfoScreenArtworkUiModelMapper,
    private val releaseDateFormatter: GameReleaseDateFormatter,
    private val ratingFormatter: GameRatingFormatter,
    private val likeCountCalculator: LikeCountCalculator,
    private val ageRatingFormatter: GameAgeRatingFormatter,
    private val categoryFormatter: GameCategoryFormatter,
) : InfoScreenHeaderUiModelMapper {

    override fun mapToUiModel(game: Game, isLiked: Boolean): InfoScreenHeaderUiModel {
        return InfoScreenHeaderUiModel(
            artworks = game.createArtworks(),
            isLiked = isLiked,
            coverImageUrl = game.createCoverImageUrl(),
            title = game.name,
            releaseDate = game.formatReleaseDate(),
            developerName = game.developerCompany?.name,
            rating = game.formatRating(),
            likeCount = game.calculateLikeCount(),
            ageRating = game.formatAgeRating(),
            gameCategory = game.formatCategory()
        )
    }

    private fun Game.createArtworks(): List<InfoScreenArtworkUiModel> {
        return artworkModelMapper.mapToUiModels(artworks)
    }

    private fun Game.createCoverImageUrl(): String? {
        return cover?.let { cover ->
            igdbImageUrlFactory.createUrl(
                cover,
                IgdbImageUrlFactory.Config(IgdbImageSize.BIG_COVER)
            )
        }
    }

    private fun Game.formatReleaseDate(): String {
        return releaseDateFormatter.formatReleaseDate(this)
    }

    private fun Game.formatRating(): String {
        return ratingFormatter.formatRating(totalRating)
    }

    private fun Game.calculateLikeCount(): String {
        return likeCountCalculator.calculateLikeCount(this).toString()
    }

    private fun Game.formatCategory(): String {
        return categoryFormatter.formatCategory(category)
    }

    private fun Game.formatAgeRating(): String {
        return ageRatingFormatter.formatAgeRating(this)
    }
}
