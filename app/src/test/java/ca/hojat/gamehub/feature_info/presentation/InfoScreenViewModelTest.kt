package ca.hojat.gamehub.feature_info.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import ca.hojat.gamehub.common_ui.widgets.FiniteUiState
import ca.hojat.gamehub.feature_info.GAME_INFO
import ca.hojat.gamehub.feature_info.domain.entities.InfoScreenData
import ca.hojat.gamehub.feature_info.presentation.widgets.companies.InfoScreenCompanyUiModel
import ca.hojat.gamehub.feature_info.presentation.widgets.header.InfoScreenHeaderUiModel
import ca.hojat.gamehub.feature_info.presentation.widgets.links.InfoScreenLinkUiModel
import ca.hojat.gamehub.feature_info.presentation.widgets.main.InfoScreenUiModel
import ca.hojat.gamehub.feature_info.presentation.widgets.main.InfoScreenUiModelMapper
import ca.hojat.gamehub.feature_info.presentation.widgets.relatedgames.RelatedGameUiModel
import ca.hojat.gamehub.feature_info.presentation.widgets.videos.InfoScreenVideoUiModel
import ca.hojat.gamehub.core.common_testing.FakeErrorMapper
import ca.hojat.gamehub.core.common_testing.FakeStringProvider
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_ERROR_UNKNOWN
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.common_ui.base.events.GeneralCommand
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class InfoScreenViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(StandardTestDispatcher())

    private val useCases = setupUseCases()

    private val sut by lazy {
        InfoScreenViewModel(
            savedStateHandle = setupSavedStateHandle(),
            transitionAnimationDuration = 0L,
            useCases = useCases,
            uiModelMapper = FakeInfoScreenUiModelMapper(),
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            stringProvider = FakeStringProvider(),
            errorMapper = FakeErrorMapper(),
        )
    }

    private fun setupUseCases(): InfoScreenUseCases {
        return InfoScreenUseCases(
            getGameInfoUseCase = mockk(relaxed = true),
            getGameImageUrlsUseCase = mockk(relaxed = true),
            toggleLikeStateUseCase = mockk(relaxed = true),
        )
    }

    private fun setupSavedStateHandle(): SavedStateHandle {
        return mockk(relaxed = true) {
            every { get<Int>(any()) } returns 1
        }
    }

    @Test
    fun `Emits correct ui states when loading data`() {
        runTest {
            coEvery { useCases.getGameInfoUseCase.execute(any()) } returns flowOf(GAME_INFO)

            sut.uiState.test {
                assertThat(awaitItem().finiteUiState).isEqualTo(FiniteUiState.EMPTY)
                assertThat(awaitItem().finiteUiState).isEqualTo(FiniteUiState.LOADING)
                assertThat(awaitItem().finiteUiState).isEqualTo(FiniteUiState.SUCCESS)
            }
        }
    }

    @Test
    fun `Dispatches toast showing command when game fetching use case throws error`() {
        runTest {
            coEvery {
                useCases.getGameInfoUseCase.execute(any())
            } returns flow { throw IllegalStateException() }

            sut.commandFlow.test {
                assertThat(awaitItem()).isInstanceOf(GeneralCommand.ShowLongToast::class.java)
            }
        }
    }

    @Test
    fun `Routes to image viewer screen when artwork is clicked`() {
        runTest {
            coEvery { useCases.getGameImageUrlsUseCase.execute(any()) } returns flowOf(Ok(listOf()))

            val artworkIndex = 10

            sut.routeFlow.test {
                sut.onArtworkClicked(artworkIndex = artworkIndex)

                val route = awaitItem()

                assertThat(route).isInstanceOf(InfoScreenRoute.ImageViewer::class.java)
                assertThat((route as InfoScreenRoute.ImageViewer).initialPosition).isEqualTo(
                    artworkIndex
                )
            }
        }
    }

    @Test
    fun `Dispatches toast showing command when artwork is clicked and image url use case throws error`() {
        runTest {
            coEvery { useCases.getGameImageUrlsUseCase.execute(any()) } returns flowOf(
                Err(
                    DOMAIN_ERROR_UNKNOWN
                )
            )

            sut
            advanceUntilIdle()

            sut.commandFlow.test {
                sut.onArtworkClicked(artworkIndex = 0)

                assertThat(awaitItem()).isInstanceOf(GeneralCommand.ShowLongToast::class.java)
            }
        }
    }

    @Test
    fun `Routes to previous screen when back button is clicked`() {
        runTest {
            sut.routeFlow.test {
                sut.onBackButtonClicked()

                assertThat(awaitItem()).isInstanceOf(InfoScreenRoute.Back::class.java)
            }
        }
    }

    @Test
    fun `Routes to image viewer screen when cover is clicked`() {
        runTest {
            coEvery { useCases.getGameImageUrlsUseCase.execute(any()) } returns flowOf(Ok(listOf()))

            sut.routeFlow.test {
                sut.onCoverClicked()

                assertThat(awaitItem()).isInstanceOf(InfoScreenRoute.ImageViewer::class.java)
            }
        }
    }

    @Test
    fun `Dispatches toast showing command when cover is clicked and image url use case throws error`() {
        runTest {
            coEvery { useCases.getGameImageUrlsUseCase.execute(any()) } returns flowOf(
                Err(
                    DOMAIN_ERROR_UNKNOWN
                )
            )

            sut
            advanceUntilIdle()

            sut.commandFlow.test {
                sut.onCoverClicked()

                assertThat(awaitItem()).isInstanceOf(GeneralCommand.ShowLongToast::class.java)
            }
        }
    }

    @Test
    fun `Dispatches url opening command when video is clicked`() {
        runTest {
            val video = InfoScreenVideoUiModel(
                id = "1",
                thumbnailUrl = "thumbnail_url",
                videoUrl = "video_url",
                title = "title",
            )

            sut.commandFlow.test {
                sut.onVideoClicked(video)

                val command = awaitItem()

                assertThat(command).isInstanceOf(InfoScreenCommand.OpenUrl::class.java)
                assertThat((command as InfoScreenCommand.OpenUrl).url).isEqualTo(video.videoUrl)
            }
        }
    }

    @Test
    fun `Routes to image viewer screen when screenshot is clicked`() {
        runTest {
            coEvery { useCases.getGameImageUrlsUseCase.execute(any()) } returns flowOf(Ok(listOf()))

            val screenshotIndex = 10

            sut.routeFlow.test {
                sut.onScreenshotClicked(screenshotIndex = screenshotIndex)

                val route = awaitItem()

                assertThat(route).isInstanceOf(InfoScreenRoute.ImageViewer::class.java)
                assertThat((route as InfoScreenRoute.ImageViewer).initialPosition).isEqualTo(
                    screenshotIndex
                )
            }
        }
    }

    @Test
    fun `Dispatches toast showing command when screenshot is clicked and image url use case throws error`() {
        runTest {
            coEvery { useCases.getGameImageUrlsUseCase.execute(any()) } returns flowOf(
                Err(
                    DOMAIN_ERROR_UNKNOWN
                )
            )

            sut
            advanceUntilIdle()

            sut.commandFlow.test {
                sut.onScreenshotClicked(screenshotIndex = 0)

                assertThat(awaitItem()).isInstanceOf(GeneralCommand.ShowLongToast::class.java)
            }
        }
    }

    @Test
    fun `Dispatches url opening command when game link is clicked`() {
        runTest {
            val link = InfoScreenLinkUiModel(
                id = 1,
                text = "text",
                iconId = 0,
                url = "url",
            )

            sut.commandFlow.test {
                sut.onLinkClicked(link)

                val command = awaitItem()

                assertThat(command).isInstanceOf(InfoScreenCommand.OpenUrl::class.java)
                assertThat((command as InfoScreenCommand.OpenUrl).url).isEqualTo(link.url)
            }
        }
    }

    @Test
    fun `Dispatches url opening command when company is clicked`() {
        runTest {
            val company = InfoScreenCompanyUiModel(
                id = 1,
                logoWidth = null,
                logoHeight = null,
                logoUrl = null,
                websiteUrl = "url",
                name = "",
                roles = "",
            )

            sut.commandFlow.test {
                sut.onCompanyClicked(company)

                val command = awaitItem()

                assertThat(command).isInstanceOf(InfoScreenCommand.OpenUrl::class.java)
                assertThat((command as InfoScreenCommand.OpenUrl).url).isEqualTo(company.websiteUrl)
            }
        }
    }

    @Test
    fun `Routes to game info when related game is clicked`() {
        runTest {
            val relatedGame = RelatedGameUiModel(
                id = 1,
                title = "title",
                coverUrl = "url",
            )

            sut.routeFlow.test {
                sut.onRelatedGameClicked(relatedGame)

                val route = awaitItem()

                assertThat(route).isInstanceOf(InfoScreenRoute.InfoScreen::class.java)
                assertThat((route as InfoScreenRoute.InfoScreen).id).isEqualTo(relatedGame.id)
            }
        }
    }

    private class FakeInfoScreenUiModelMapper : InfoScreenUiModelMapper {

        override fun mapToUiModel(infoScreenData: InfoScreenData): InfoScreenUiModel {
            return InfoScreenUiModel(
                id = 1,
                headerModel = InfoScreenHeaderUiModel(
                    artworks = emptyList(),
                    isLiked = true,
                    coverImageUrl = null,
                    title = "title",
                    releaseDate = "release_date",
                    developerName = null,
                    rating = "rating",
                    likeCount = "like_count",
                    ageRating = "age_rating",
                    gameCategory = "game_category"
                ),
                videoModels = emptyList(),
                screenshotModels = emptyList(),
                summary = null,
                detailsModel = null,
                linkModels = emptyList(),
                companyModels = emptyList(),
                otherCompanyGames = null,
                similarGames = null
            )
        }
    }
}
