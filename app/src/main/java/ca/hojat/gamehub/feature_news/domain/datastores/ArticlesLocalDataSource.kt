package ca.hojat.gamehub.feature_news.domain.datastores

import ca.hojat.gamehub.core.domain.entities.Pagination
import ca.hojat.gamehub.feature_news.domain.entities.Article
import kotlinx.coroutines.flow.Flow

interface ArticlesLocalDataSource {
    suspend fun saveArticles(articles: List<Article>)
    fun observeArticles(pagination: Pagination): Flow<List<Article>>
}
