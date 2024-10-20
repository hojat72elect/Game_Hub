package ca.hojat.gamehub.feature_news.domain.datastores

import javax.inject.Inject

class ArticlesDataStores @Inject constructor(
    val local: ArticlesLocalDataSource,
    val remote: ArticlesRemoteDataSource
)
