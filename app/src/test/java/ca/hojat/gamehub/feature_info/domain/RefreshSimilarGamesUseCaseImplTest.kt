package ca.hojat.gamehub.feature_info.domain

import app.cash.turbine.test
import ca.hojat.gamehub.feature_info.REFRESH_SIMILAR_GAMES_USE_CASE_PARAMS
import ca.hojat.gamehub.core.domain.games.common.throttling.GamesRefreshingThrottler
import ca.hojat.gamehub.core.domain.games.common.throttling.GamesRefreshingThrottlerTools
import ca.hojat.gamehub.core.domain.games.repository.GamesRepository
import ca.hojat.gamehub.core.domain.games.repository.GamesLocalDataSource
import ca.hojat.gamehub.core.domain.games.repository.GamesRemoteDataSource
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_ERROR_UNKNOWN
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAMES
import ca.hojat.gamehub.core.common_testing.domain.FakeGamesRefreshingThrottlerKeyProvider
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.common_testing.domain.coVerifyNotCalled
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_info.domain.usecases.game.RefreshSimilarGamesUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RefreshSimilarGamesUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var gamesLocalDataSource: GamesLocalDataSource
    @MockK
    private lateinit var gamesRemoteDataSource: GamesRemoteDataSource
    @MockK
    private lateinit var throttler: GamesRefreshingThrottler

    private lateinit var sut: RefreshSimilarGamesUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        sut = RefreshSimilarGamesUseCaseImpl(
            gamesRepository = GamesRepository(
                local = gamesLocalDataSource,
                remote = gamesRemoteDataSource
            ),
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            throttlerTools = GamesRefreshingThrottlerTools(
                throttler = throttler,
                keyProvider = FakeGamesRefreshingThrottlerKeyProvider()
            ),
        )
    }

    @Test
    fun `Emits remote games when refresh is possible`() {
        runTest {
            coEvery { throttler.canRefreshSimilarGames(any()) } returns true
            coEvery { gamesRemoteDataSource.getSimilarGames(any(), any()) } returns Ok(DOMAIN_GAMES)

            sut.execute(REFRESH_SIMILAR_GAMES_USE_CASE_PARAMS).test {
                assertThat(awaitItem().get()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Does not emit remote games when refresh is not possible`() {
        runTest {
            coEvery { throttler.canRefreshSimilarGames(any()) } returns false

            sut.execute(REFRESH_SIMILAR_GAMES_USE_CASE_PARAMS).test {
                awaitComplete()
            }
        }
    }

    @Test
    fun `Saves remote games into local data store when refresh is successful`() {
        runTest {
            coEvery { throttler.canRefreshSimilarGames(any()) } returns true
            coEvery { gamesRemoteDataSource.getSimilarGames(any(), any()) } returns Ok(DOMAIN_GAMES)

            sut.execute(REFRESH_SIMILAR_GAMES_USE_CASE_PARAMS).firstOrNull()

            coVerify { gamesLocalDataSource.saveGames(DOMAIN_GAMES) }
        }
    }

    @Test
    fun `Does not save remote games into local data store when refresh is not possible`() {
        runTest {
            coEvery { throttler.canRefreshSimilarGames(any()) } returns false

            sut.execute(REFRESH_SIMILAR_GAMES_USE_CASE_PARAMS).firstOrNull()

            coVerifyNotCalled { gamesLocalDataSource.saveGames(any()) }
        }
    }

    @Test
    fun `Does not save remote games into local data store when refresh is unsuccessful`() {
        runTest {
            coEvery { throttler.canRefreshSimilarGames(any()) } returns false
            coEvery { gamesRemoteDataSource.getSimilarGames(any(), any()) } returns Err(
                DOMAIN_ERROR_UNKNOWN
            )

            sut.execute(REFRESH_SIMILAR_GAMES_USE_CASE_PARAMS).firstOrNull()

            coVerifyNotCalled { gamesLocalDataSource.saveGames(any()) }
        }
    }

    @Test
    fun `Updates games last refresh time when refresh is successful`() {
        runTest {
            coEvery { throttler.canRefreshSimilarGames(any()) } returns true
            coEvery { gamesRemoteDataSource.getSimilarGames(any(), any()) } returns Ok(DOMAIN_GAMES)

            sut.execute(REFRESH_SIMILAR_GAMES_USE_CASE_PARAMS).firstOrNull()

            coVerify { throttler.updateGamesLastRefreshTime(any()) }
        }
    }

    @Test
    fun `Does not update games last refresh time when refresh is not possible`() {
        runTest {
            coEvery { throttler.canRefreshSimilarGames(any()) } returns false

            sut.execute(REFRESH_SIMILAR_GAMES_USE_CASE_PARAMS).firstOrNull()

            coVerifyNotCalled { throttler.updateGamesLastRefreshTime(any()) }
        }
    }

    @Test
    fun `Does not update games last refresh time when refresh is unsuccessful`() {
        runTest {
            coEvery { throttler.canRefreshSimilarGames(any()) } returns false
            coEvery { gamesRemoteDataSource.getSimilarGames(any(), any()) } returns Err(
                DOMAIN_ERROR_UNKNOWN
            )

            sut.execute(REFRESH_SIMILAR_GAMES_USE_CASE_PARAMS).firstOrNull()

            coVerifyNotCalled { throttler.updateGamesLastRefreshTime(any()) }
        }
    }
}
