package ca.hojat.gamehub.feature_info.presentation.widgets.header.artworks

import androidx.compose.runtime.Immutable

private const val DEFAULT_IMAGE_ID = "default_image_id"

@Immutable
sealed class InfoScreenArtworkUiModel(open val id: String) {
    /**
     * This game doesn't have an artwork image at all, so we show a default image of our own.
     */
    object DefaultImage : InfoScreenArtworkUiModel(id = DEFAULT_IMAGE_ID)
    data class UrlImage(override val id: String, val url: String) : InfoScreenArtworkUiModel(id)
}
