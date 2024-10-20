package ca.hojat.gamehub.feature_image_viewer

import androidx.compose.runtime.Immutable

/**
 *@param gameName : The name of the game for which we are showing the photos.
 */
@Immutable
data class ImageViewerUiState(
    val gameName: String,
    val toolbarTitle: String,
    val imageUrls: List<String>,
    val selectedImageUrlIndex: Int,
)
