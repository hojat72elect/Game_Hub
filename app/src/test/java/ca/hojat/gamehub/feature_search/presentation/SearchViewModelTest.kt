package ca.hojat.gamehub.feature_search.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import ca.hojat.gamehub.common_ui.widgets.FiniteUiState
import ca.hojat.gamehub.common_ui.widgets.games.GameUiModel
import ca.hojat.gamehub.common_ui.widgets.games.GameUiModelMapper
import ca.hojat.gamehub.core.common_testing.FakeErrorMapper
import ca.hojat.gamehub.core.common_testing.FakeStringProvider
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_ERROR_UNKNOWN
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAMES
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.common_ui.base.events.GeneralCommand
import ca.hojat.gamehub.core.domain.entities.Game
import ca.hojat.gamehub.feature_search.domain.SearchGamesUseCase
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(StandardTestDispatcher())

    private val searchGamesUseCase = mockk<SearchGamesUseCase>(relaxed = true)

    private val sut by lazy {
        GamesSearchViewModel(
            searchGamesUseCase = searchGamesUseCase,
            uiModelMapper = FakeGameUiModelMapper(),
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            stringProvider = FakeStringProvider(),
            errorMapper = FakeErrorMapper(),
            savedStateHandle = setupSavedStateHandle(),
        )
    }

    private fun setupSavedStateHandle(): SavedStateHandle {
        return mockk(relaxed = true) {
            every { get<String>(any()) } returns ""
        }
    }

    @Test
    fun `Routes to previous screen when toolbar back button is clicked`() {
        runTest {
            sut.routeFlow.test {
                sut.onToolbarBackButtonClicked()

                assertThat(awaitItem()).isInstanceOf(GamesSearchRoute.Back::class.java)
            }
        }
    }

    @Test
    fun `Query text is cleared from UI state when clear button is clicked`() {
        runTest {
            val query = "query"

            sut.onQueryChanged(query)

            sut.uiState.test {
                sut.onToolbarClearButtonClicked()

                assertThat(awaitItem().queryText).isEqualTo(query)
                assertThat(awaitItem().queryText).isEmpty()
            }
        }
    }

    @Test
    fun `Query text of UI state is synced with query text entered by user`() {
        runTest {
            val query = "Shadow of the Colossus"

            sut.uiState.test {
                sut.onQueryChanged(query)

                assertThat(awaitItem().queryText).isEmpty()
                assertThat(awaitItem().queryText).isEqualTo(query)
            }
        }
    }

    @Test
    fun `Emits correct ui states when searching for games`() {
        runTest {
            coEvery { searchGamesUseCase.execute(any()) } returns flowOf(Ok(DOMAIN_GAMES))

            sut.uiState.test {
                sut.onSearchConfirmed("god of war")

                val emptyState = awaitItem().gamesUiState
                val loadingState = awaitItem().gamesUiState
                val resultState = awaitItem().gamesUiState

                assertThat(emptyState.finiteUiState).isEqualTo(FiniteUiState.EMPTY)
                assertThat(loadingState.finiteUiState).isEqualTo(FiniteUiState.LOADING)
                assertThat(resultState.finiteUiState).isEqualTo(FiniteUiState.SUCCESS)
                assertThat(resultState.games).hasSize(DOMAIN_GAMES.size)
            }
        }
    }

    @Test
    fun `Does not emit ui states when search query is empty`() {
        runTest {
            sut.uiState.test {
                sut.onSearchConfirmed("")

                assertThat(awaitItem().gamesUiState.finiteUiState).isEqualTo(FiniteUiState.EMPTY)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `Does not emit ui states when the current search query is provided`() {
        runTest {
            coEvery { searchGamesUseCase.execute(any()) } returns flowOf(Ok(DOMAIN_GAMES))

            val gameQuery = "god of war"

            sut.onSearchConfirmed(gameQuery)
            advanceUntilIdle()

            sut.uiState.test {
                sut.onSearchConfirmed(gameQuery)

                assertThat(awaitItem().gamesUiState.finiteUiState).isEqualTo(FiniteUiState.SUCCESS)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `Emits empty ui state when blank search query is provided`() {
        runTest {
            sut.uiState.test {
                sut.onSearchConfirmed("   ")

                assertThat(awaitItem().gamesUiState.finiteUiState).isEqualTo(FiniteUiState.EMPTY)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `Dispatches items clearing command when performing new search`() {
        runTest {
            coEvery { searchGamesUseCase.execute(any()) } returns flowOf(Ok(DOMAIN_GAMES))

            sut.uiState.test {
                sut.onSearchConfirmed("god of war")

                assertThat(awaitItem().gamesUiState.games).isEmpty()
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `Dispatches toast showing command when searching games use case throws error`() {
        runTest {
            coEvery { searchGamesUseCase.execute(any()) } returns flowOf(Err(DOMAIN_ERROR_UNKNOWN))

            sut.commandFlow.test {
                sut.onSearchConfirmed("god of war")

                assertThat(awaitItem()).isInstanceOf(GeneralCommand.ShowLongToast::class.java)
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

                assertThat(route).isInstanceOf(GamesSearchRoute.Info::class.java)
                assertThat((route as GamesSearchRoute.Info).gameId).isEqualTo(game.id)
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
                developerName = "developer_name",
                description = "description",
            )
        }
    }
}
