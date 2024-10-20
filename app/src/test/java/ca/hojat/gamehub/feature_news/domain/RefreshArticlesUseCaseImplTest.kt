package ca.hojat.gamehub.feature_news.domain

import app.cash.turbine.test
import ca.hojat.gamehub.feature_news.DOMAIN_ARTICLES
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_ERROR_UNKNOWN
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.common_testing.domain.coVerifyNotCalled
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_news.domain.throttling.ArticlesRefreshingThrottler
import ca.hojat.gamehub.feature_news.domain.throttling.ArticlesRefreshingThrottlerKeyProvider
import ca.hojat.gamehub.feature_news.domain.throttling.ArticlesRefreshingThrottlerTools
import ca.hojat.gamehub.feature_news.domain.usecases.RefreshArticlesUseCase
import ca.hojat.gamehub.feature_news.domain.usecases.RefreshArticlesUseCaseImpl
import ca.hojat.gamehub.feature_news.domain.datastores.ArticlesDataStores
import ca.hojat.gamehub.feature_news.domain.datastores.ArticlesLocalDataSource
import ca.hojat.gamehub.feature_news.domain.datastores.ArticlesRemoteDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val USE_CASE_PARAMS = RefreshArticlesUseCase.Params()

class RefreshArticlesUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var articlesLocalDataSource: ArticlesLocalDataSource
    @MockK
    private lateinit var articlesRemoteDataSource: ArticlesRemoteDataSource
    @MockK
    private lateinit var throttler: ArticlesRefreshingThrottler
    @MockK
    private lateinit var keyProvider: ArticlesRefreshingThrottlerKeyProvider

    private lateinit var sut: RefreshArticlesUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        sut = RefreshArticlesUseCaseImpl(
            articlesDataStores = ArticlesDataStores(
                local = articlesLocalDataSource,
                remote = articlesRemoteDataSource
            ),
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            throttlerTools = ArticlesRefreshingThrottlerTools(
                throttler = throttler,
                keyProvider = keyProvider
            ),
        )

        every { keyProvider.provideArticlesKey(any()) } returns "key"
    }

    @Test
    fun `Emits remote articles when refresh is possible`() {
        runTest {
            coEvery { throttler.canRefreshArticles(any()) } returns true
            coEvery { articlesRemoteDataSource.getArticles(any()) } returns Ok(DOMAIN_ARTICLES)

            sut.execute(USE_CASE_PARAMS).test {
                assertThat(awaitItem().get()).isEqualTo(DOMAIN_ARTICLES)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Does not emit remote articles when refresh is not possible`() {
        runTest {
            coEvery { throttler.canRefreshArticles(any()) } returns false

            sut.execute(USE_CASE_PARAMS).test {
                awaitComplete()
            }
        }
    }

    @Test
    fun `Saves remote articles into local data store when refresh is successful`() {
        runTest {
            coEvery { throttler.canRefreshArticles(any()) } returns true
            coEvery { articlesRemoteDataSource.getArticles(any()) } returns Ok(DOMAIN_ARTICLES)

            sut.execute(USE_CASE_PARAMS).firstOrNull()

            coVerify { articlesLocalDataSource.saveArticles(DOMAIN_ARTICLES) }
        }
    }

    @Test
    fun `Does not save remote articles into local data store when refresh is not possible`() {
        runTest {
            coEvery { throttler.canRefreshArticles(any()) } returns false

            sut.execute(USE_CASE_PARAMS).firstOrNull()

            coVerifyNotCalled { articlesLocalDataSource.saveArticles(any()) }
        }
    }

    @Test
    fun `Does not save remote articles into local data store when refresh is unsuccessful`() {
        runTest {
            coEvery { throttler.canRefreshArticles(any()) } returns false
            coEvery { articlesRemoteDataSource.getArticles(any()) } returns Err(DOMAIN_ERROR_UNKNOWN)

            sut.execute(USE_CASE_PARAMS).firstOrNull()

            coVerifyNotCalled { articlesLocalDataSource.saveArticles(any()) }
        }
    }

    @Test
    fun `Updates articles last refresh time when refresh is successful`() {
        runTest {
            coEvery { throttler.canRefreshArticles(any()) } returns true
            coEvery { articlesRemoteDataSource.getArticles(any()) } returns Ok(DOMAIN_ARTICLES)

            sut.execute(USE_CASE_PARAMS).firstOrNull()

            coVerify { throttler.updateArticlesLastRefreshTime(any()) }
        }
    }

    @Test
    fun `Does not update articles last refresh time when refresh is not possible`() {
        runTest {
            coEvery { throttler.canRefreshArticles(any()) } returns false

            sut.execute(USE_CASE_PARAMS).firstOrNull()

            coVerifyNotCalled { throttler.updateArticlesLastRefreshTime(any()) }
        }
    }

    @Test
    fun `Does not update articles last refresh time when refresh is unsuccessful`() {
        runTest {
            coEvery { throttler.canRefreshArticles(any()) } returns false
            coEvery { articlesRemoteDataSource.getArticles(any()) } returns Err(DOMAIN_ERROR_UNKNOWN)

            sut.execute(USE_CASE_PARAMS).firstOrNull()

            coVerifyNotCalled { throttler.updateArticlesLastRefreshTime(any()) }
        }
    }
}
