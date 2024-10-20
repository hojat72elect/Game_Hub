package ca.hojat.gamehub.feature_discovery

import app.cash.turbine.test
import ca.hojat.gamehub.common_ui.base.events.GeneralCommand
import ca.hojat.gamehub.core.common_testing.FakeErrorMapper
import ca.hojat.gamehub.core.common_testing.FakeStringProvider
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAMES
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.domain.entities.Game
import ca.hojat.gamehub.core.domain.games.usecases.ObservePopularGamesUseCase
import ca.hojat.gamehub.core.domain.games.usecases.RefreshPopularGamesUseCase
import ca.hojat.gamehub.feature_discovery.di.DiscoverKey
import ca.hojat.gamehub.feature_discovery.widgets.DiscoverItemModelMapper
import ca.hojat.gamehub.feature_discovery.widgets.DiscoverScreenItemData
import com.github.michaelbull.result.Ok
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class DiscoverViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(StandardTestDispatcher())

    private val observePopularGamesUseCase = mockk<ObservePopularGamesUseCase>(relaxed = true)
    private val refreshPopularGamesUseCase = mockk<RefreshPopularGamesUseCase>(relaxed = true)

    private val sut by lazy {
        DiscoverViewModel(
            useCases = setupUseCases(),
            uiModelMapper = FakeDiscoverItemModelMapper(),
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            stringProvider = FakeStringProvider(),
            errorMapper = FakeErrorMapper()
        )
    }

    private fun setupUseCases(): DiscoverUseCases {
        return DiscoverUseCases(
            observeGamesUseCasesMap = mapOf(
                DiscoverKey.Type.POPULAR to observePopularGamesUseCase,
                DiscoverKey.Type.RECENTLY_RELEASED to mockk {
                    every { execute(any()) } returns flowOf(DOMAIN_GAMES)
                },
                DiscoverKey.Type.COMING_SOON to mockk {
                    every { execute(any()) } returns flowOf(DOMAIN_GAMES)
                },
                DiscoverKey.Type.MOST_ANTICIPATED to mockk {
                    every { execute(any()) } returns flowOf(DOMAIN_GAMES)
                }
            ),
            refreshGamesUseCasesMap = mapOf(
                DiscoverKey.Type.POPULAR to refreshPopularGamesUseCase,
                DiscoverKey.Type.RECENTLY_RELEASED to mockk {
                    every { execute(any()) } returns flowOf(Ok(DOMAIN_GAMES))
                },
                DiscoverKey.Type.COMING_SOON to mockk {
                    every { execute(any()) } returns flowOf(Ok(DOMAIN_GAMES))
                },
                DiscoverKey.Type.MOST_ANTICIPATED to mockk {
                    every { execute(any()) } returns flowOf(Ok(DOMAIN_GAMES))
                }
            )
        )
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
    fun `Routes to search screen when search button is clicked`() {
        runTest {
            sut.routeFlow.test {
                sut.onSearchButtonClicked()

                assertThat(awaitItem()).isInstanceOf(DiscoverScreenRoute.Search::class.java)
            }
        }
    }

    @Test
    fun `Routes to games category screen when more button is clicked`() {
        runTest {
            val categoryName = DiscoverType.POPULAR.name

            sut.routeFlow.test {
                sut.onCategoryMoreButtonClicked(categoryName)

                val route = awaitItem()

                assertThat(route).isInstanceOf(DiscoverScreenRoute.Category::class.java)
                assertThat((route as DiscoverScreenRoute.Category).category).isEqualTo(categoryName)
            }
        }
    }

    @Test
    fun `Routes to game info screen when game is clicked`() {
        runTest {
            val item = DiscoverScreenItemData(
                id = 1,
                title = "title",
                coverUrl = null
            )

            sut.routeFlow.test {
                sut.onCategoryItemClicked(item)

                val route = awaitItem()

                assertThat(route).isInstanceOf(DiscoverScreenRoute.Info::class.java)
                assertThat((route as DiscoverScreenRoute.Info).itemId).isEqualTo(item.id)
            }
        }
    }

    private class FakeDiscoverItemModelMapper : DiscoverItemModelMapper() {

        override fun mapToUiModel(game: Game): DiscoverScreenItemData {
            return DiscoverScreenItemData(
                id = game.id,
                title = game.name,
                coverUrl = null
            )
        }
    }
}
