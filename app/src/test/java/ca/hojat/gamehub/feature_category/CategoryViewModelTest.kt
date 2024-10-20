package ca.hojat.gamehub.feature_category

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import ca.hojat.gamehub.common_ui.base.events.GeneralCommand
import ca.hojat.gamehub.common_ui.widgets.FiniteUiState
import ca.hojat.gamehub.core.common_testing.FakeErrorMapper
import ca.hojat.gamehub.core.common_testing.FakeStringProvider
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAMES
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.domain.entities.Game
import ca.hojat.gamehub.core.domain.games.usecases.ObservePopularGamesUseCase
import ca.hojat.gamehub.core.domain.games.usecases.RefreshPopularGamesUseCase
import ca.hojat.gamehub.feature_category.di.CategoryKey
import ca.hojat.gamehub.feature_category.widgets.CategoryUiModel
import ca.hojat.gamehub.feature_category.widgets.CategoryItemModelMapper
import com.github.michaelbull.result.Ok
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import javax.inject.Provider

class CategoryViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(StandardTestDispatcher())

    private val observePopularGamesUseCase = mockk<ObservePopularGamesUseCase>(relaxed = true)
    private val refreshPopularGamesUseCase = mockk<RefreshPopularGamesUseCase>(relaxed = true)

    private val sut by lazy {
        CategoryViewModel(
            savedStateHandle = setupSavedStateHandle(),
            stringProvider = FakeStringProvider(),
            transitionAnimationDuration = 0L,
            useCases = setupUseCases(),
            uiModelMapper = FakeCategoryItemModelMapper(),
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            errorMapper = FakeErrorMapper()
        )
    }

    private fun setupSavedStateHandle(): SavedStateHandle {
        return mockk(relaxed = true) {
            every { get<String>(any()) } returns CategoryType.POPULAR.name
        }
    }

    private fun setupUseCases(): CategoryUseCases {
        return CategoryUseCases(
            observeGamesUseCasesMap = mapOf(
                CategoryKey.Type.POPULAR to Provider { observePopularGamesUseCase },
                CategoryKey.Type.RECENTLY_RELEASED to Provider(::mockk),
                CategoryKey.Type.COMING_SOON to Provider(::mockk),
                CategoryKey.Type.MOST_ANTICIPATED to Provider(::mockk)
            ),
            refreshGamesUseCasesMap = mapOf(
                CategoryKey.Type.POPULAR to Provider { refreshPopularGamesUseCase },
                CategoryKey.Type.RECENTLY_RELEASED to Provider(::mockk),
                CategoryKey.Type.COMING_SOON to Provider(::mockk),
                CategoryKey.Type.MOST_ANTICIPATED to Provider(::mockk)
            )
        )
    }

    @Test
    fun `Emits toolbar title when initialized`() {
        runTest {
            sut.uiState.test {
                assertThat(awaitItem().title).isNotEmpty()
            }
        }
    }

    @Test
    fun `Emits correct ui states when observing games`() {
        runTest {
            every { observePopularGamesUseCase.execute(any()) } returns flowOf(DOMAIN_GAMES)

            sut.uiState.test {
                assertThat(awaitItem().finiteUiState).isEqualTo(FiniteUiState.EMPTY)
                assertThat(awaitItem().finiteUiState).isEqualTo(FiniteUiState.SUCCESS)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `Dispatches toast showing command when games observing use case throws error`() {
        runTest {
            every { observePopularGamesUseCase.execute(any()) } returns flow {
                throw IllegalStateException(
                    "error"
                )
            }
            every { refreshPopularGamesUseCase.execute(any()) } returns flowOf(Ok(DOMAIN_GAMES))

            sut.commandFlow.test {
                assertThat(awaitItem()).isInstanceOf(GeneralCommand.ShowLongToast::class.java)
            }
        }
    }

    @Test
    fun `Emits correct ui states when refreshing games`() {
        runTest {
            every {
                refreshPopularGamesUseCase.execute(any())
            } returns flow {
                // Refresh, for some reason, emits way too fast.
                // Adding delay to grab all possible states.
                delay(10)
                emit(Ok(DOMAIN_GAMES))
            }

            sut.uiState.test {
                assertThat(awaitItem().finiteUiState).isEqualTo(FiniteUiState.EMPTY)
                assertThat(awaitItem().finiteUiState).isEqualTo(FiniteUiState.LOADING)
                assertThat(awaitItem().finiteUiState).isEqualTo(FiniteUiState.EMPTY)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `Dispatches toast showing command when games refreshing use case throws error`() {
        runTest {
            every { observePopularGamesUseCase.execute(any()) } returns flowOf(DOMAIN_GAMES)
            every { refreshPopularGamesUseCase.execute(any()) } returns flow {
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
    fun `Routes to previous screen when toolbar left button is clicked`() {
        runTest {
            sut.routeFlow.test {
                sut.onToolbarLeftButtonClicked()

                assertThat(awaitItem()).isInstanceOf(CategoryScreenRoute.Back::class.java)
            }
        }
    }

    @Test
    fun `Routes to game info screen when game is clicked`() {
        runTest {
            val game = CategoryUiModel(
                id = 1,
                title = "title",
                coverUrl = null
            )

            sut.routeFlow.test {
                sut.onItemClicked(game)

                val route = awaitItem()

                assertThat(route).isInstanceOf(CategoryScreenRoute.Info::class.java)
                assertThat((route as CategoryScreenRoute.Info).gameId).isEqualTo(game.id)
            }
        }
    }

    private class FakeCategoryItemModelMapper : CategoryItemModelMapper() {

        override fun mapToUiModel(game: Game): CategoryUiModel {
            return CategoryUiModel(
                id = game.id,
                title = game.name,
                coverUrl = null,
            )
        }
    }
}
