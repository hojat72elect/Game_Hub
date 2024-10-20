package ca.hojat.gamehub.feature_news.data

import ca.hojat.gamehub.core.common_testing.API_ERROR_HTTP
import ca.hojat.gamehub.core.common_testing.API_ERROR_NETWORK
import ca.hojat.gamehub.core.common_testing.API_ERROR_UNKNOWN
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.common_testing.domain.PAGINATION
import ca.hojat.gamehub.core.data.api.ApiErrorMapper
import ca.hojat.gamehub.core.data.api.gamespot.articles.ArticlesEndpoint
import ca.hojat.gamehub.core.data.api.gamespot.articles.entities.ApiArticle
import ca.hojat.gamehub.feature_news.data.datastores.gamespot.ArticlePublicationDateMapper
import ca.hojat.gamehub.feature_news.data.datastores.gamespot.ArticlesGamespotDataSource
import ca.hojat.gamehub.feature_news.data.datastores.gamespot.GamespotArticleMapper
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val API_ARTICLES = listOf(
    ApiArticle(publicationDate = "2020-03-02 12:14:16"),
    ApiArticle(publicationDate = "2020-03-02 12:14:16"),
    ApiArticle(publicationDate = "2020-03-02 12:14:16")
)

class ArticlesGamespotDataStoreTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var articlesEndpoint: ArticlesEndpoint

    private lateinit var apiArticleMapper: GamespotArticleMapper
    private lateinit var apiErrorMapper: ApiErrorMapper
    private lateinit var sut: ArticlesGamespotDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        apiArticleMapper = GamespotArticleMapper(ArticlePublicationDateMapper())
        apiErrorMapper = ApiErrorMapper()
        sut = ArticlesGamespotDataSource(
            articlesEndpoint = articlesEndpoint,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            apiArticleMapper = apiArticleMapper,
            apiErrorMapper = apiErrorMapper,
        )
    }

    @Test
    fun `Returns articles successfully`() {
        runTest {
            coEvery { articlesEndpoint.getArticles(any(), any()) } returns Ok(API_ARTICLES)

            val result = sut.getArticles(PAGINATION)

            assertThat(result.get())
                .isEqualTo(apiArticleMapper.mapToDomainArticles(API_ARTICLES))
        }
    }

    @Test
    fun `Returns http error when fetching articles`() {
        runTest {
            coEvery { articlesEndpoint.getArticles(any(), any()) } returns Err(API_ERROR_HTTP)

            val result = sut.getArticles(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_HTTP))
        }
    }

    @Test
    fun `Returns network error when fetching articles`() {
        runTest {
            coEvery { articlesEndpoint.getArticles(any(), any()) } returns Err(API_ERROR_NETWORK)

            val result = sut.getArticles(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_NETWORK))
        }
    }

    @Test
    fun `Returns unknown error when fetching articles`() {
        runTest {
            coEvery { articlesEndpoint.getArticles(any(), any()) } returns Err(API_ERROR_UNKNOWN)

            val result = sut.getArticles(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_UNKNOWN))
        }
    }
}
