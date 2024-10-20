package ca.hojat.gamehub.feature_news.data.datastores.gamespot

import android.os.Build
import androidx.annotation.RequiresApi
import ca.hojat.gamehub.feature_news.domain.DomainArticle
import ca.hojat.gamehub.feature_news.domain.DomainImageType
import ca.hojat.gamehub.core.data.api.gamespot.articles.entities.ApiArticle
import ca.hojat.gamehub.core.data.api.gamespot.articles.entities.ApiImageType
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class GamespotArticleMapper @Inject constructor(
    private val publicationDateMapper: ArticlePublicationDateMapper,
) {
    private fun mapToDomainArticle(apiArticle: ApiArticle): DomainArticle {
        return DomainArticle(
            id = apiArticle.id,
            body = apiArticle.body,
            title = apiArticle.title,
            lede = apiArticle.lede,
            imageUrls = apiArticle.imageUrls.toDataImageUrls(),
            publicationDate = publicationDateMapper.mapToTimestamp(apiArticle.publicationDate),
            siteDetailUrl = apiArticle.siteDetailUrl
        )
    }

    private fun Map<ApiImageType, String>.toDataImageUrls(): Map<DomainImageType, String> {
        return mapKeys {
            DomainImageType.valueOf(it.key.name)
        }
    }

    fun mapToDomainArticles(apiArticles: List<ApiArticle>): List<DomainArticle> {
        return apiArticles.map(::mapToDomainArticle)
    }
}
