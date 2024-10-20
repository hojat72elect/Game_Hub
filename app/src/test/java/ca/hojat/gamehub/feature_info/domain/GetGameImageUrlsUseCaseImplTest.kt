package ca.hojat.gamehub.feature_info.domain

import app.cash.turbine.test
import ca.hojat.gamehub.core.factories.ImageViewerGameUrlFactory
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAME
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_info.domain.entities.GameImageType
import ca.hojat.gamehub.feature_info.domain.usecases.game.GetGameImageUrlsUseCase
import ca.hojat.gamehub.feature_info.domain.usecases.game.GetGameImageUrlsUseCaseImpl
import ca.hojat.gamehub.feature_info.domain.usecases.game.GetGameUseCase
import ca.hojat.gamehub.core.domain.entities.Error
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val USE_CASE_PARAMS = GetGameImageUrlsUseCase.Params(
    gameId = 0,
    gameImageType = GameImageType.COVER,
)

class GetGameImageUrlsUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var getGameUseCase: GetGameUseCase
    @MockK
    private lateinit var gameUrlFactory: ImageViewerGameUrlFactory

    private lateinit var sut: GetGameImageUrlsUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = GetGameImageUrlsUseCaseImpl(
            getGameUseCase = getGameUseCase,
            gameUrlFactory = gameUrlFactory,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits game cover image url successfully`() {
        runTest {
            val expectedImageUrl = "cover_image_url"

            coEvery { getGameUseCase.execute(any()) } returns flowOf(Ok(DOMAIN_GAME))
            coEvery { gameUrlFactory.createCoverImageUrl(DOMAIN_GAME) } returns expectedImageUrl

            sut.execute(USE_CASE_PARAMS.copy(gameImageType = GameImageType.COVER)).test {
                val actualImageUrl = awaitItem()

                assertThat(actualImageUrl.get()).isNotNull()
                assertThat(actualImageUrl.get()!!.first()).isEqualTo(expectedImageUrl)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Emits error when game cover image url cannot be created`() {
        runTest {
            coEvery { getGameUseCase.execute(any()) } returns flowOf(Ok(DOMAIN_GAME))
            coEvery { gameUrlFactory.createCoverImageUrl(DOMAIN_GAME) } returns null

            sut.execute(USE_CASE_PARAMS.copy(gameImageType = GameImageType.COVER)).test {
                val errorResult = awaitItem()

                assertThat(errorResult.getError()).isNotNull()
                assertThat(errorResult.getError()).isInstanceOf(Error.Unknown::class.java)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Emits game artwork image urls successfully`() {
        runTest {
            val expectedArtworkImageUrls = listOf(
                "artwork_image_url_1",
                "artwork_image_url_2",
                "artwork_image_url_3",
            )

            coEvery { getGameUseCase.execute(any()) } returns flowOf(Ok(DOMAIN_GAME))
            coEvery { gameUrlFactory.createArtworkImageUrls(DOMAIN_GAME) } returns expectedArtworkImageUrls

            sut.execute(USE_CASE_PARAMS.copy(gameImageType = GameImageType.ARTWORK)).test {
                val actualArtworkImageUrls = awaitItem()

                assertThat(actualArtworkImageUrls.get()).isNotNull()
                assertThat(actualArtworkImageUrls.get()).isEqualTo(expectedArtworkImageUrls)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Emits game screenshot image urls successfully`() {
        runTest {
            val expectedScreenshotImageUrls = listOf(
                "screenshot_image_url_1",
                "screenshot_image_url_2",
                "screenshot_image_url_3",
            )

            coEvery { getGameUseCase.execute(any()) } returns flowOf(Ok(DOMAIN_GAME))
            coEvery { gameUrlFactory.createScreenshotImageUrls(DOMAIN_GAME) } returns expectedScreenshotImageUrls

            sut.execute(USE_CASE_PARAMS.copy(gameImageType = GameImageType.SCREENSHOT)).test {
                val actualScreenshotImageUrls = awaitItem()

                assertThat(actualScreenshotImageUrls.get()).isNotNull()
                assertThat(actualScreenshotImageUrls.get()).isEqualTo(expectedScreenshotImageUrls)
                awaitComplete()
            }
        }
    }
}
