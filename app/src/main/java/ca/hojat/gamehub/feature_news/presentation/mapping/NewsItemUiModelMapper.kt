package ca.hojat.gamehub.feature_news.presentation.mapping

import ca.hojat.gamehub.core.formatters.ArticlePublicationDateFormatter
import ca.hojat.gamehub.feature_news.domain.entities.Article
import ca.hojat.gamehub.feature_news.domain.entities.ImageType
import ca.hojat.gamehub.feature_news.presentation.widgets.NewsItemUiModel
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

abstract class NewsItemUiModelMapper {
    abstract fun mapToUiModel(article: Article): NewsItemUiModel

    /**
     * Just maps from the [Article] in our domain, to [NewsItemUiModel] in UI layer.
     * we do this to make the data ready to be shown in composables.
     */
    fun mapToUiModels(
        articles: List<Article>,
    ): List<NewsItemUiModel> {
        return articles.map(::mapToUiModel)
    }
}

@BindType(installIn = BindType.Component.VIEW_MODEL)
class NewsItemUiModelMapperImpl @Inject constructor(
    private val publicationDateFormatter: ArticlePublicationDateFormatter
) : NewsItemUiModelMapper() {

    override fun mapToUiModel(article: Article): NewsItemUiModel {
        return NewsItemUiModel(
            id = article.id,
            imageUrl = article.imageUrls[ImageType.ORIGINAL],
            title = article.title,
            body = article.body,
            lede = article.lede,
            publicationDate = article.formatPublicationDate(),
            siteDetailUrl = article.siteDetailUrl
        )
    }

    private fun Article.formatPublicationDate(): String {
        return publicationDateFormatter.formatPublicationDate(publicationDate)
    }
}
