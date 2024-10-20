package ca.hojat.gamehub.feature_news.data.datastores.database

import ca.hojat.gamehub.core.data.database.articles.ArticlesTable
import ca.hojat.gamehub.core.data.database.articles.DbArticle
import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.entities.Pagination
import ca.hojat.gamehub.feature_news.domain.datastores.ArticlesLocalDataSource
import ca.hojat.gamehub.feature_news.domain.entities.Article
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BindType
class ArticlesDatabaseDataSource @Inject constructor(
    private val articlesTable: ArticlesTable,
    private val dispatcherProvider: DispatcherProvider,
    private val dbArticleMapper: DbArticleMapper
) : ArticlesLocalDataSource {

    override suspend fun saveArticles(articles: List<Article>) {
        articlesTable.saveArticles(
            withContext(dispatcherProvider.computation) {
                dbArticleMapper.mapToDatabaseArticles(articles)
            }
        )
    }

    override fun observeArticles(pagination: Pagination): Flow<List<Article>> {
        return articlesTable.observeArticles(
            offset = pagination.offset,
            limit = pagination.limit
        )
            .toDataArticlesFlow()
    }

    private fun Flow<List<DbArticle>>.toDataArticlesFlow(): Flow<List<Article>> {
        return distinctUntilChanged()
            .map(dbArticleMapper::mapToDomainArticles)
            .flowOn(dispatcherProvider.computation)
    }
}
