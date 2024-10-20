package ca.hojat.gamehub.core.data.games.igdb

import ca.hojat.gamehub.core.data.api.ApiErrorMapper
import ca.hojat.gamehub.core.data.api.igdb.games.entities.ApiGame
import ca.hojat.gamehub.core.data.api.igdb.games.GamesEndpoint
import ca.hojat.gamehub.core.data.games.datastores.GamesIgdbDataSource
import ca.hojat.gamehub.core.data.games.datastores.IgdbGameMapper
import ca.hojat.gamehub.core.data.DOMAIN_COMPANY
import ca.hojat.gamehub.core.data.FakeDiscoveryGamesReleaseDatesProvider
import ca.hojat.gamehub.core.data.games.datastores.mapToDomainGames
import ca.hojat.gamehub.core.common_testing.API_ERROR_HTTP
import ca.hojat.gamehub.core.common_testing.API_ERROR_NETWORK
import ca.hojat.gamehub.core.common_testing.API_ERROR_UNKNOWN
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAME
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import ca.hojat.gamehub.core.common_testing.domain.PAGINATION
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val API_GAMES = listOf(
    ApiGame(id = 1, name = "name1"),
    ApiGame(id = 2, name = "name2"),
    ApiGame(id = 3, name = "name3"),
)

class GamesIgdbDataStoreTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var gamesEndpoint: GamesEndpoint

    private lateinit var igdbGameMapper: IgdbGameMapper
    private lateinit var apiErrorMapper: ApiErrorMapper
    private lateinit var sut: GamesIgdbDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        igdbGameMapper = IgdbGameMapper()
        apiErrorMapper = ApiErrorMapper()
        sut = GamesIgdbDataSource(
            gamesEndpoint = gamesEndpoint,
            releaseDatesProvider = FakeDiscoveryGamesReleaseDatesProvider(),
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
            igdbGameMapper = igdbGameMapper,
            apiErrorMapper = apiErrorMapper,
        )
    }

    @Test
    fun `Returns searched games successfully`() {
        runTest {
            coEvery { gamesEndpoint.searchGames(any()) } returns Ok(API_GAMES)

            val result = sut.searchGames("query", PAGINATION)

            assertThat(result.get())
                .isEqualTo(igdbGameMapper.mapToDomainGames(API_GAMES))
        }
    }

    @Test
    fun `Returns http error when searching games`() {
        runTest {
            coEvery { gamesEndpoint.searchGames(any()) } returns Err(API_ERROR_HTTP)

            val result = sut.searchGames("query", PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_HTTP))
        }
    }

    @Test
    fun `Returns network error when searching games`() {
        runTest {
            coEvery { gamesEndpoint.searchGames(any()) } returns Err(API_ERROR_NETWORK)

            val result = sut.searchGames("query", PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_NETWORK))
        }
    }

    @Test
    fun `Returns unknown error when searching games`() {
        runTest {
            coEvery { gamesEndpoint.searchGames(any()) } returns Err(API_ERROR_UNKNOWN)

            val result = sut.searchGames("query", PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_UNKNOWN))
        }
    }

    @Test
    fun `Returns popular games successfully`() {
        runTest {
            coEvery { gamesEndpoint.getPopularGames(any()) } returns Ok(API_GAMES)

            val result = sut.getPopularGames(PAGINATION)

            assertThat(result.get())
                .isEqualTo(igdbGameMapper.mapToDomainGames(API_GAMES))
        }
    }

    @Test
    fun `Returns http error when fetching popular games`() {
        runTest {
            coEvery { gamesEndpoint.getPopularGames(any()) } returns Err(API_ERROR_HTTP)

            val result = sut.getPopularGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_HTTP))
        }
    }

    @Test
    fun `Returns network error when fetching popular games`() {
        runTest {
            coEvery { gamesEndpoint.getPopularGames(any()) } returns Err(API_ERROR_NETWORK)

            val result = sut.getPopularGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_NETWORK))
        }
    }

    @Test
    fun `Returns unknown error when fetching popular games`() {
        runTest {
            coEvery { gamesEndpoint.getPopularGames(any()) } returns Err(API_ERROR_UNKNOWN)

            val result = sut.getPopularGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_UNKNOWN))
        }
    }

    @Test
    fun `Returns recently released games successfully`() {
        runTest {
            coEvery { gamesEndpoint.getRecentlyReleasedGames(any()) } returns Ok(API_GAMES)

            val result = sut.getRecentlyReleasedGames(PAGINATION)

            assertThat(result.get())
                .isEqualTo(igdbGameMapper.mapToDomainGames(API_GAMES))
        }
    }

    @Test
    fun `Returns http error when fetching recently released games`() {
        runTest {
            coEvery { gamesEndpoint.getRecentlyReleasedGames(any()) } returns Err(API_ERROR_HTTP)

            val result = sut.getRecentlyReleasedGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_HTTP))
        }
    }

    @Test
    fun `Returns network error when fetching recently released games`() {
        runTest {
            coEvery { gamesEndpoint.getRecentlyReleasedGames(any()) } returns Err(API_ERROR_NETWORK)

            val result = sut.getRecentlyReleasedGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_NETWORK))
        }
    }

    @Test
    fun `Returns unknown error when fetching recently released games`() {
        runTest {
            coEvery { gamesEndpoint.getRecentlyReleasedGames(any()) } returns Err(API_ERROR_UNKNOWN)

            val result = sut.getRecentlyReleasedGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_UNKNOWN))
        }
    }

    @Test
    fun `Returns coming soon games successfully`() {
        runTest {
            coEvery { gamesEndpoint.getComingSoonGames(any()) } returns Ok(API_GAMES)

            val result = sut.getComingSoonGames(PAGINATION)

            assertThat(result.get())
                .isEqualTo(igdbGameMapper.mapToDomainGames(API_GAMES))
        }
    }

    @Test
    fun `Returns http error when fetching coming soon games`() {
        runTest {
            coEvery { gamesEndpoint.getComingSoonGames(any()) } returns Err(API_ERROR_HTTP)

            val result = sut.getComingSoonGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_HTTP))
        }
    }

    @Test
    fun `Returns network error when fetching coming soon games`() {
        runTest {
            coEvery { gamesEndpoint.getComingSoonGames(any()) } returns Err(API_ERROR_NETWORK)

            val result = sut.getComingSoonGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_NETWORK))
        }
    }

    @Test
    fun `Returns unknown error when fetching coming soon games`() {
        runTest {
            coEvery { gamesEndpoint.getComingSoonGames(any()) } returns Err(API_ERROR_UNKNOWN)

            val result = sut.getComingSoonGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_UNKNOWN))
        }
    }

    @Test
    fun `Returns most anticipated games successfully`() {
        runTest {
            coEvery { gamesEndpoint.getMostAnticipatedGames(any()) } returns Ok(API_GAMES)

            val result = sut.getMostAnticipatedGames(PAGINATION)

            assertThat(result.get())
                .isEqualTo(igdbGameMapper.mapToDomainGames(API_GAMES))
        }
    }

    @Test
    fun `Returns http error when fetching most anticipated games`() {
        runTest {
            coEvery { gamesEndpoint.getMostAnticipatedGames(any()) } returns Err(API_ERROR_HTTP)

            val result = sut.getMostAnticipatedGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_HTTP))
        }
    }

    @Test
    fun `Returns network error when fetching most anticipated games`() {
        runTest {
            coEvery { gamesEndpoint.getMostAnticipatedGames(any()) } returns Err(API_ERROR_NETWORK)

            val result = sut.getMostAnticipatedGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_NETWORK))
        }
    }

    @Test
    fun `Returns unknown error when fetching most anticipated games`() {
        runTest {
            coEvery { gamesEndpoint.getMostAnticipatedGames(any()) } returns Err(API_ERROR_UNKNOWN)

            val result = sut.getMostAnticipatedGames(PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_UNKNOWN))
        }
    }

    @Test
    fun `Returns company developed games successfully`() {
        runTest {
            coEvery { gamesEndpoint.getGames(any()) } returns Ok(API_GAMES)

            val result = sut.getCompanyDevelopedGames(DOMAIN_COMPANY, PAGINATION)

            assertThat(result.get())
                .isEqualTo(igdbGameMapper.mapToDomainGames(API_GAMES))
        }
    }

    @Test
    fun `Returns http error when fetching company developed games`() {
        runTest {
            coEvery { gamesEndpoint.getGames(any()) } returns Err(API_ERROR_HTTP)

            val result = sut.getCompanyDevelopedGames(DOMAIN_COMPANY, PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_HTTP))
        }
    }

    @Test
    fun `Returns network error when fetching company developed games`() {
        runTest {
            coEvery { gamesEndpoint.getGames(any()) } returns Err(API_ERROR_NETWORK)

            val result = sut.getCompanyDevelopedGames(DOMAIN_COMPANY, PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_NETWORK))
        }
    }

    @Test
    fun `Returns unknown error when fetching company developed games`() {
        runTest {
            coEvery { gamesEndpoint.getGames(any()) } returns Err(API_ERROR_UNKNOWN)

            val result = sut.getCompanyDevelopedGames(DOMAIN_COMPANY, PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_UNKNOWN))
        }
    }

    @Test
    fun `Returns similar games successfully`() {
        runTest {
            coEvery { gamesEndpoint.getGames(any()) } returns Ok(API_GAMES)

            val result = sut.getSimilarGames(DOMAIN_GAME, PAGINATION)

            assertThat(result.get())
                .isEqualTo(igdbGameMapper.mapToDomainGames(API_GAMES))
        }
    }

    @Test
    fun `Returns http error when fetching similar games`() {
        runTest {
            coEvery { gamesEndpoint.getGames(any()) } returns Err(API_ERROR_HTTP)

            val result = sut.getSimilarGames(DOMAIN_GAME, PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_HTTP))
        }
    }

    @Test
    fun `Returns network error when fetching similar games`() {
        runTest {
            coEvery { gamesEndpoint.getGames(any()) } returns Err(API_ERROR_NETWORK)

            val result = sut.getSimilarGames(DOMAIN_GAME, PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_NETWORK))
        }
    }

    @Test
    fun `Returns unknown error when fetching similar games`() {
        runTest {
            coEvery { gamesEndpoint.getGames(any()) } returns Err(API_ERROR_UNKNOWN)

            val result = sut.getSimilarGames(DOMAIN_GAME, PAGINATION)

            assertThat(result.getError())
                .isEqualTo(apiErrorMapper.mapToDomainError(API_ERROR_UNKNOWN))
        }
    }
}
