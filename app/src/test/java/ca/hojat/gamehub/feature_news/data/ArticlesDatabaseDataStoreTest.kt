package ca.hojat.gamehub.feature_news.data

import app.cash.turbine.test
import ca.hojat.gamehub.feature_news.DOMAIN_ARTICLES
import ca.hojat.gamehub.core.data.database.articles.ArticlesTable
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.common_testing.domain.PAGINATION
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_news.data.datastores.database.ArticlesDatabaseDataSource
import ca.hojat.gamehub.feature_news.data.datastores.database.DbArticleMapper
import ca.hojat.gamehub.feature_news.data.datastores.database.mapToDatabaseArticles
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ArticlesDatabaseDataStoreTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var articlesTable: ArticlesTable

    private lateinit var dbArticleMapper: DbArticleMapper
    private lateinit var sut: ArticlesDatabaseDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        dbArticleMapper = DbArticleMapper()
        sut = ArticlesDatabaseDataSource(
            articlesTable = articlesTable,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            dbArticleMapper = dbArticleMapper,
        )
    }

    @Test
    fun `Saves articles to table successfully`() {
        runTest {
            sut.saveArticles(DOMAIN_ARTICLES)

            coVerify {
                articlesTable.saveArticles(dbArticleMapper.mapToDatabaseArticles(DOMAIN_ARTICLES))
            }
        }
    }

    @Test
    fun `Emits articles successfully`() {
        runTest {
            val databaseArticles = dbArticleMapper.mapToDatabaseArticles(DOMAIN_ARTICLES)

            every { articlesTable.observeArticles(any(), any()) } returns flowOf(databaseArticles)

            sut.observeArticles(PAGINATION).test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_ARTICLES)
                awaitComplete()
            }
        }
    }
}
