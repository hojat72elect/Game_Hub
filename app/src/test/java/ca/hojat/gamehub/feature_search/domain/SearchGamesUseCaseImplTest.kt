package ca.hojat.gamehub.feature_search.domain

import app.cash.turbine.test
import ca.hojat.gamehub.core.providers.NetworkStateProvider
import ca.hojat.gamehub.core.domain.games.repository.GamesRepository
import ca.hojat.gamehub.core.domain.games.repository.GamesLocalDataSource
import ca.hojat.gamehub.core.domain.games.repository.GamesRemoteDataSource
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_ERROR_UNKNOWN
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAMES
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.common_testing.domain.PAGINATION
import ca.hojat.gamehub.core.common_testing.domain.coVerifyNotCalled
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val SEARCH_GAMES_USE_CASE_PARAMS = SearchGamesUseCase.Params(
    searchQuery = "search_query",
    pagination = PAGINATION,
)

class SearchGamesUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var gamesLocalDataSource: GamesLocalDataSource
    @MockK
    private lateinit var gamesRemoteDataSource: GamesRemoteDataSource
    @MockK
    private lateinit var networkStateProvider: NetworkStateProvider

    private lateinit var sut: SearchGamesUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        sut = SearchGamesUseCaseImpl(
            gamesRepository = GamesRepository(
                local = gamesLocalDataSource,
                remote = gamesRemoteDataSource,
            ),
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            networkStateProvider = networkStateProvider,
        )
    }

    @Test
    fun `Emits from remote data store when network is on`() {
        runTest {
            every { networkStateProvider.isNetworkAvailable } returns true
            coEvery { gamesRemoteDataSource.searchGames(any(), any()) } returns Ok(DOMAIN_GAMES)

            sut.execute(SEARCH_GAMES_USE_CASE_PARAMS).test {
                assertThat(awaitItem().get()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Saves remote games into local data store when network is on & request succeeds`() {
        runTest {
            every { networkStateProvider.isNetworkAvailable } returns true
            coEvery { gamesRemoteDataSource.searchGames(any(), any()) } returns Ok(DOMAIN_GAMES)

            sut.execute(SEARCH_GAMES_USE_CASE_PARAMS).firstOrNull()

            coVerify { gamesLocalDataSource.saveGames(DOMAIN_GAMES) }
        }
    }

    @Test
    fun `Does not save remote games into local data store when network is on & request fails`() {
        runTest {
            every { networkStateProvider.isNetworkAvailable } returns true
            coEvery { gamesRemoteDataSource.searchGames(any(), any()) } returns Err(
                DOMAIN_ERROR_UNKNOWN
            )

            sut.execute(SEARCH_GAMES_USE_CASE_PARAMS).firstOrNull()

            coVerifyNotCalled { gamesLocalDataSource.saveGames(any()) }
        }
    }

    @Test
    fun `Emits from local data store when network is off`() {
        runTest {
            every { networkStateProvider.isNetworkAvailable } returns false
            coEvery { gamesLocalDataSource.searchGames(any(), any()) } returns DOMAIN_GAMES

            sut.execute(SEARCH_GAMES_USE_CASE_PARAMS).test {
                assertThat(awaitItem().get()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }
}
