package ca.hojat.gamehub.feature_info.domain

import app.cash.turbine.test
import ca.hojat.gamehub.feature_info.GET_SIMILAR_GAMES_USE_CASE_PARAMS
import ca.hojat.gamehub.core.domain.games.repository.GamesLocalDataSource
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAMES
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_info.domain.usecases.game.RefreshSimilarGamesUseCase
import ca.hojat.gamehub.feature_info.domain.usecases.game.GetSimilarGamesUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetSimilarGamesUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var refreshSimilarGamesUseCase: RefreshSimilarGamesUseCase
    @MockK
    private lateinit var gamesLocalDataSource: GamesLocalDataSource

    private lateinit var sut: GetSimilarGamesUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = GetSimilarGamesUseCaseImpl(
            refreshSimilarGamesUseCase = refreshSimilarGamesUseCase,
            gamesLocalDataSource = gamesLocalDataSource,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits games that refresh use case successfully emits`() {
        runTest {
            coEvery { refreshSimilarGamesUseCase.execute(any()) } returns flowOf(Ok(DOMAIN_GAMES))

            sut.execute(GET_SIMILAR_GAMES_USE_CASE_PARAMS).test {
                assertThat(awaitItem().get()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Emits games from local data store if refresh use case does not emit`() {
        runTest {
            coEvery { refreshSimilarGamesUseCase.execute(any()) } returns flowOf()
            coEvery { gamesLocalDataSource.getSimilarGames(any(), any()) } returns DOMAIN_GAMES

            sut.execute(GET_SIMILAR_GAMES_USE_CASE_PARAMS).test {
                assertThat(awaitItem().get()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }
}
