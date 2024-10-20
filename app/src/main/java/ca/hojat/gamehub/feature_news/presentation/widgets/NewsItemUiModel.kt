package ca.hojat.gamehub.feature_news.presentation.widgets

import androidx.compose.runtime.Immutable

@Immutable
data class NewsItemUiModel(
    val id: Int,
    val imageUrl: String?,
    val title: String,
    val body: String,
    val lede: String,
    val publicationDate: String,
    val siteDetailUrl: String
) {

    val hasImageUrl: Boolean
        get() = imageUrl != null
}
