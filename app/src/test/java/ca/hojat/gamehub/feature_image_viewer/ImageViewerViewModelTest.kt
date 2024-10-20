package ca.hojat.gamehub.feature_image_viewer

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import ca.hojat.gamehub.core.common_testing.FakeStringProvider
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

private const val INITIAL_POSITION = 0

class ImageViewerViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(StandardTestDispatcher())

    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true) {
        every { get<String>(PARAM_TITLE) } returns ""
        every { get<String>(PARAM_GAME_NAME) } returns ""
        every { get<Int>(PARAM_INITIAL_POSITION) } returns INITIAL_POSITION
        every { get<String>(PARAM_IMAGE_URLS) } returns "url1,url2,url3"
        every { get<Int>(KEY_SELECTED_POSITION) } returns INITIAL_POSITION
    }

    private val sut by lazy {
        ImageViewerViewModel(
            stringProvider = FakeStringProvider(),
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `Throws error when image urls are not provided`() {
        every { savedStateHandle.get<String>(PARAM_IMAGE_URLS) } returns null

        assertThrows(IllegalStateException::class.java) {
            sut
        }
    }

    @Test
    fun `Dispatches text sharing command when toolbar share button is clicked`() {
        runTest {
            sut.commandFlow.test {
                sut.onShareButtonClicked()

                assertThat(awaitItem()).isInstanceOf(ImageViewerCommand.ShareText::class.java)
            }
        }
    }

    @Test
    fun `Dispatches download command when toolbar download button is clicked`() {
        runTest {
            sut.commandFlow.test {
                sut.onDownloadButtonClicked()

                assertThat(awaitItem()).isInstanceOf(ImageViewerCommand.DownloadFile::class.java)
            }
        }
    }

    @Test
    fun `Emits selected position when page is changed`() {
        runTest {
            val selectedPosition = 10

            sut.uiState.test {
                sut.onImageChanged(selectedPosition)

                assertThat(awaitItem().selectedImageUrlIndex).isEqualTo(INITIAL_POSITION)
                assertThat(awaitItem().selectedImageUrlIndex).isEqualTo(selectedPosition)
            }
        }
    }

    @Test
    fun `Emits toolbar title when page is changed`() {
        runTest {
            sut.onImageChanged(10)
            advanceUntilIdle()

            sut.uiState.test {
                assertThat(awaitItem().toolbarTitle).isNotEmpty()
            }
        }
    }

    @Test
    fun `Routes to previous screen when back button is clicked`() {
        runTest {
            sut.routeFlow.test {
                sut.onBackPressed()

                assertThat(awaitItem()).isInstanceOf(ImageViewerRoute.Back::class.java)
            }
        }
    }
}
