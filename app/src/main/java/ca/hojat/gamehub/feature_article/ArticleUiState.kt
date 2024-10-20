package ca.hojat.gamehub.feature_article

import androidx.compose.runtime.Immutable

@Immutable
data class ArticleUiState(
    val imageUrl: String?,
    val title: String,
    val lede: String,
    val publicationDate: String,
    val articleUrl: String,
    val body: String
)
