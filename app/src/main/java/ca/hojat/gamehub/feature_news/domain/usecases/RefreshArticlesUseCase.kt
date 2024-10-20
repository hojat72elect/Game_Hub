package ca.hojat.gamehub.feature_news.domain.usecases

import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.DomainResult
import ca.hojat.gamehub.core.extensions.onEachSuccess
import ca.hojat.gamehub.core.domain.entities.Pagination
import ca.hojat.gamehub.core.domain.common.usecases.ObservableUseCase
import ca.hojat.gamehub.feature_news.domain.datastores.ArticlesDataStores
import ca.hojat.gamehub.feature_news.domain.entities.Article
import ca.hojat.gamehub.feature_news.domain.throttling.ArticlesRefreshingThrottlerTools
import ca.hojat.gamehub.feature_news.domain.usecases.RefreshArticlesUseCase.Params
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * You give it an object of type [Params] and will receive a [Flow<DomainResult<List<Article>>>].
 */
interface RefreshArticlesUseCase : ObservableUseCase<Params, DomainResult<List<Article>>> {

    data class Params(val pagination: Pagination = Pagination())
}

@Singleton
@BindType
class RefreshArticlesUseCaseImpl @Inject constructor(
    private val articlesDataStores: ArticlesDataStores,
    private val dispatcherProvider: DispatcherProvider,
    private val throttlerTools: ArticlesRefreshingThrottlerTools,
) : RefreshArticlesUseCase {

    override fun execute(params: Params): Flow<DomainResult<List<Article>>> {
        val throttlerKey = throttlerTools.keyProvider.provideArticlesKey(params.pagination)

        return flow {
            if (throttlerTools.throttler.canRefreshArticles(throttlerKey)) {
                emit(articlesDataStores.remote.getArticles(params.pagination))
            }
        }
            .onEachSuccess { articles ->
                articlesDataStores.local.saveArticles(articles)
                throttlerTools.throttler.updateArticlesLastRefreshTime(throttlerKey)
            }
            .flowOn(dispatcherProvider.main)
    }
}
