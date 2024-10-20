package ca.hojat.gamehub.feature_news.data.datastores.gamespot

import android.os.Build
import androidx.annotation.RequiresApi
import ca.hojat.gamehub.core.data.api.ApiErrorMapper
import ca.hojat.gamehub.core.domain.DomainResult
import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.entities.Pagination
import ca.hojat.gamehub.feature_news.domain.datastores.ArticlesRemoteDataSource
import ca.hojat.gamehub.feature_news.domain.entities.Article
import ca.hojat.gamehub.core.data.api.common.ApiResult
import ca.hojat.gamehub.core.data.api.gamespot.articles.ArticlesEndpoint
import ca.hojat.gamehub.core.data.api.gamespot.articles.entities.ApiArticle
import com.github.michaelbull.result.mapEither
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
@BindType
class ArticlesGamespotDataSource @Inject constructor(
    private val articlesEndpoint: ArticlesEndpoint,
    private val dispatcherProvider: DispatcherProvider,
    private val apiArticleMapper: GamespotArticleMapper,
    private val apiErrorMapper: ApiErrorMapper,
) : ArticlesRemoteDataSource {

    override suspend fun getArticles(pagination: Pagination): DomainResult<List<Article>> {
        return articlesEndpoint
            .getArticles(pagination.offset, pagination.limit)
            .toDataStoreResult()
    }


    private suspend fun ApiResult<List<ApiArticle>>.toDataStoreResult(): DomainResult<List<Article>> {
        return withContext(dispatcherProvider.computation) {
            mapEither(apiArticleMapper::mapToDomainArticles, apiErrorMapper::mapToDomainError)
        }
    }
}
