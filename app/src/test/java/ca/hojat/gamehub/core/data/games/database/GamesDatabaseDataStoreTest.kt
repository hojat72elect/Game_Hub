package ca.hojat.gamehub.core.data.games.database

import app.cash.turbine.test
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAME
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAMES
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.common_testing.domain.PAGINATION
import ca.hojat.gamehub.core.data.DOMAIN_COMPANY
import ca.hojat.gamehub.core.data.FakeDiscoveryGamesReleaseDatesProvider
import ca.hojat.gamehub.core.data.database.games.tables.GamesTable
import ca.hojat.gamehub.core.data.games.datastores.DbGameMapper
import ca.hojat.gamehub.core.data.games.datastores.GamesDatabaseDataSource
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GamesDatabaseDataStoreTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var gamesTable: GamesTable

    private lateinit var dbGameMapper: DbGameMapper
    private lateinit var sut: GamesDatabaseDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        dbGameMapper = DbGameMapper()
        sut = GamesDatabaseDataSource(
            gamesTable = gamesTable,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            discoveryGamesReleaseDatesProvider = FakeDiscoveryGamesReleaseDatesProvider(),
            dbGameMapper = dbGameMapper,
        )
    }

    @Test
    fun `Saves games to table successfully`() {
        runTest {
            sut.saveGames(DOMAIN_GAMES)

            coVerify { gamesTable.saveGames(dbGameMapper.mapToDatabaseGames(DOMAIN_GAMES)) }
        }
    }

    @Test
    fun `Retrieves game successfully`() {
        runTest {
            val dbGame = dbGameMapper.mapToDatabaseGame(DOMAIN_GAME)

            coEvery { gamesTable.getGame(any()) } returns dbGame

            assertThat(sut.getGame(DOMAIN_GAME.id)).isEqualTo(DOMAIN_GAME)
        }
    }

    @Test
    fun `Retrieves null instead of game`() {
        runTest {
            val gameId = DOMAIN_GAME.id

            coEvery { gamesTable.getGame(gameId) } returns null

            assertThat(sut.getGame(gameId)).isNull()
        }
    }

    @Test
    fun `Retrieves company developed games successfully`() {
        runTest {
            val dbGames = dbGameMapper.mapToDatabaseGames(DOMAIN_GAMES)

            coEvery { gamesTable.getGames(any(), any(), any()) } returns dbGames

            assertThat(sut.getCompanyDevelopedGames(DOMAIN_COMPANY, PAGINATION))
                .isEqualTo(DOMAIN_GAMES)
        }
    }

    @Test
    fun `Retrieves similar games successfully`() {
        runTest {
            val dbGames = dbGameMapper.mapToDatabaseGames(DOMAIN_GAMES)

            coEvery { gamesTable.getGames(any(), any(), any()) } returns dbGames

            assertThat(sut.getSimilarGames(DOMAIN_GAME, PAGINATION))
                .isEqualTo(DOMAIN_GAMES)
        }
    }

    @Test
    fun `Searches games successfully`() {
        runTest {
            val dbGames = dbGameMapper.mapToDatabaseGames(DOMAIN_GAMES)

            coEvery { gamesTable.searchGames(any(), any(), any()) } returns dbGames

            assertThat(sut.searchGames("", PAGINATION)).isEqualTo(DOMAIN_GAMES)
        }
    }

    @Test
    fun `Observes popular games successfully`() {
        runTest {
            val dbGames = dbGameMapper.mapToDatabaseGames(DOMAIN_GAMES)

            every { gamesTable.observePopularGames(any(), any(), any()) } returns flowOf(dbGames)

            sut.observePopularGames(PAGINATION).test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Observes recently released games successfully`() {
        runTest {
            val dbGames = dbGameMapper.mapToDatabaseGames(DOMAIN_GAMES)

            every {
                gamesTable.observeRecentlyReleasedGames(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns flowOf(dbGames)

            sut.observeRecentlyReleasedGames(PAGINATION).test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Observes coming soon games successfully`() {
        runTest {
            val dbGames = dbGameMapper.mapToDatabaseGames(DOMAIN_GAMES)

            every { gamesTable.observeComingSoonGames(any(), any(), any()) } returns flowOf(dbGames)

            sut.observeComingSoonGames(PAGINATION).test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Observes most anticipated games successfully`() {
        runTest {
            val dbGames = dbGameMapper.mapToDatabaseGames(DOMAIN_GAMES)

            every { gamesTable.observeMostAnticipatedGames(any(), any(), any()) } returns flowOf(
                dbGames
            )

            sut.observeMostAnticipatedGames(PAGINATION).test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }
}
