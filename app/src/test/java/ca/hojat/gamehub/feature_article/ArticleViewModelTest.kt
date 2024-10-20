package ca.hojat.gamehub.feature_article

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import ca.hojat.gamehub.core.common_testing.FakeStringProvider
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ArticleViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(StandardTestDispatcher())

    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true) {
        every { get<String>(PARAM_IMAGE_URL) } returns ""
        every { get<String>(PARAM_TITLE) } returns ""
        every { get<String>(PARAM_LEDE) } returns ""
        every { get<String>(PARAM_PUBLICATION_DATE) } returns ""
        every { get<String>(PARAM_ARTICLE_URL) } returns ""
        every { get<String>(PARAM_BODY) } returns ""
    }

    private val sut by lazy {
        ArticleViewModel(
            stringProvider = FakeStringProvider(),
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `Routes to previous screen when back button is clicked`() {
        runTest {
            sut.routeFlow.test {
                sut.onBackPressed()

                assertThat(awaitItem()).isInstanceOf(ArticleRoute.Back::class.java)
            }
        }
    }

    @Test
    fun `Dispatches text sharing command when toolbar share button is clicked`() {
        runTest {
            sut.commandFlow.test {
                sut.onShareButtonClicked()

                assertThat(awaitItem()).isInstanceOf(ArticleCommand.ShareText::class.java)
            }
        }
    }
}