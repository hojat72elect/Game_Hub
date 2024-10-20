package ca.hojat.gamehub.feature_news.domain

import app.cash.turbine.test
import ca.hojat.gamehub.feature_news.DOMAIN_ARTICLES
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.common_testing.domain.PAGINATION
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_news.domain.usecases.ObserveArticlesUseCase
import ca.hojat.gamehub.feature_news.domain.usecases.ObserveArticlesUseCaseImpl
import ca.hojat.gamehub.feature_news.domain.datastores.ArticlesLocalDataSource
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val USE_CASE_PARAMS = ObserveArticlesUseCase.Params(PAGINATION)

class ObserveArticlesUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var articlesLocalDataSource: ArticlesLocalDataSource

    private lateinit var sut: ObserveArticlesUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = ObserveArticlesUseCaseImpl(
            articlesLocalDataSource = articlesLocalDataSource,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits articles from local data store`() {
        runTest {
            every { articlesLocalDataSource.observeArticles(any()) } returns flowOf(DOMAIN_ARTICLES)

            sut.execute(USE_CASE_PARAMS).test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_ARTICLES)
                awaitComplete()
            }
        }
    }
}
