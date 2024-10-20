package ca.hojat.gamehub.feature_news.domain.entities

data class Article(
    val id: Int,
    val body: String,
    val title: String,
    val lede: String,
    val imageUrls: Map<ImageType, String>,
    val publicationDate: Long,
    val siteDetailUrl: String
)
