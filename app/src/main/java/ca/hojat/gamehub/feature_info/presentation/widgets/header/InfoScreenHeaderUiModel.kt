package ca.hojat.gamehub.feature_info.presentation.widgets.header

import androidx.compose.runtime.Immutable
import ca.hojat.gamehub.feature_info.presentation.widgets.header.artworks.InfoScreenArtworkUiModel

/**
 * @param isLiked : Whether this game has been liked or not.
 * @param title: name of the game.
 */
@Immutable
data class InfoScreenHeaderUiModel(
    val artworks: List<InfoScreenArtworkUiModel>,
    val isLiked: Boolean,
    val coverImageUrl: String?,
    val title: String,
    val releaseDate: String,
    val developerName: String?,
    val rating: String,
    val likeCount: String,
    val ageRating: String,
    val gameCategory: String,
) {

    val hasCoverImageUrl: Boolean
        get() = (coverImageUrl != null)

    val hasDeveloperName: Boolean
        get() = (developerName != null)
}
