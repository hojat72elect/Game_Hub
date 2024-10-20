package ca.hojat.gamehub.feature_likes.presentation

import app.cash.turbine.test
import ca.hojat.gamehub.common_ui.widgets.FiniteUiState
import ca.hojat.gamehub.common_ui.widgets.games.GameUiModel
import ca.hojat.gamehub.common_ui.widgets.games.GameUiModelMapper
import ca.hojat.gamehub.core.common_testing.FakeErrorMapper
import ca.hojat.gamehub.core.common_testing.FakeStringProvider
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAMES
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.common_ui.base.events.GeneralCommand
import ca.hojat.gamehub.core.domain.entities.Game
import ca.hojat.gamehub.feature_likes.domain.ObserveLikedGamesUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LikesViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(StandardTestDispatcher())

    private val observeLikedGamesUseCase = mockk<ObserveLikedGamesUseCase>(relaxed = true)

    private val sut by lazy {
        LikesViewModel(
            observeLikedGamesUseCase = observeLikedGamesUseCase,
            uiModelMapper = FakeGameUiModelMapper(),
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            stringProvider = FakeStringProvider(),
            errorMapper = FakeErrorMapper()
        )
    }

    @Test
    fun `Emits correct ui states when loading data`() {
        runTest {
            every { observeLikedGamesUseCase.execute(any()) } returns flowOf(DOMAIN_GAMES)

            sut.uiState.test {
                val emptyState = awaitItem()
                val loadingState = awaitItem()
                val resultState = awaitItem()

                assertThat(emptyState.finiteUiState).isEqualTo(FiniteUiState.EMPTY)
                assertThat(loadingState.finiteUiState).isEqualTo(FiniteUiState.LOADING)
                assertThat(resultState.finiteUiState).isEqualTo(FiniteUiState.SUCCESS)
                assertThat(resultState.games).hasSize(DOMAIN_GAMES.size)
            }
        }
    }

    @Test
    fun `Dispatches toast showing command when liked games loading fails`() {
        runTest {
            every { observeLikedGamesUseCase.execute(any()) } returns flow {
                throw IllegalStateException(
                    "error"
                )
            }

            sut.commandFlow.test {
                assertThat(awaitItem()).isInstanceOf(GeneralCommand.ShowLongToast::class.java)
            }
        }
    }

    @Test
    fun `Routes to search screen when search button is clicked`() {
        runTest {
            sut.routeFlow.test {
                sut.onSearchButtonClicked()

                assertThat(awaitItem()).isInstanceOf(LikesRoute.Search::class.java)
            }
        }
    }

    @Test
    fun `Routes to info screen when game is clicked`() {
        runTest {
            val game = GameUiModel(
                id = 1,
                coverImageUrl = null,
                name = "",
                releaseDate = "",
                developerName = null,
                description = null,
            )

            sut.routeFlow.test {
                sut.onGameClicked(game)

                val route = awaitItem()

                assertThat(route).isInstanceOf(LikesRoute.Info::class.java)
                assertThat((route as LikesRoute.Info).gameId).isEqualTo(game.id)
            }
        }
    }

    private class FakeGameUiModelMapper : GameUiModelMapper {

        override fun mapToUiModel(game: Game): GameUiModel {
            return GameUiModel(
                id = game.id,
                coverImageUrl = null,
                name = game.name,
                releaseDate = "release_date",
                developerName = null,
                description = null,
            )
        }
    }
}
