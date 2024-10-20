package ca.hojat.gamehub.feature_news.data.datastores.database

import ca.hojat.gamehub.core.data.database.articles.DbArticle
import ca.hojat.gamehub.core.data.database.articles.DbImageType
import ca.hojat.gamehub.feature_news.domain.DomainArticle
import ca.hojat.gamehub.feature_news.domain.DomainImageType
import javax.inject.Inject

class DbArticleMapper @Inject constructor() {

    fun mapToDatabaseArticle(dataArticle: DomainArticle): DbArticle {
        return DbArticle(
            id = dataArticle.id,
            body = dataArticle.body,
            title = dataArticle.title,
            lede = dataArticle.lede,
            imageUrls = dataArticle.imageUrls.toDatabaseImageUrls(),
            publicationDate = dataArticle.publicationDate,
            siteDetailUrl = dataArticle.siteDetailUrl
        )
    }

    private fun Map<DomainImageType, String>.toDatabaseImageUrls(): Map<DbImageType, String> {
        return mapKeys {
            DbImageType.valueOf(it.key.name)
        }
    }

    fun mapToDomainArticle(databaseArticle: DbArticle): DomainArticle {
        return DomainArticle(
            id = databaseArticle.id,
            body = databaseArticle.body,
            title = databaseArticle.title,
            lede = databaseArticle.lede,
            imageUrls = databaseArticle.imageUrls.toDomainImageUrls(),
            publicationDate = databaseArticle.publicationDate,
            siteDetailUrl = databaseArticle.siteDetailUrl
        )
    }

    private fun Map<DbImageType, String>.toDomainImageUrls(): Map<DomainImageType, String> {
        return mapKeys {
            DomainImageType.valueOf(it.key.name)
        }
    }
}

fun DbArticleMapper.mapToDatabaseArticles(dataArticles: List<DomainArticle>): List<DbArticle> {
    return dataArticles.map(::mapToDatabaseArticle)
}

fun DbArticleMapper.mapToDomainArticles(databaseArticles: List<DbArticle>): List<DomainArticle> {
    return databaseArticles.map(::mapToDomainArticle)
}
