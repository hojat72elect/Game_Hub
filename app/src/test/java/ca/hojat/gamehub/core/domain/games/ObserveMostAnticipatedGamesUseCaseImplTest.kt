package ca.hojat.gamehub.core.domain.games

import app.cash.turbine.test
import ca.hojat.gamehub.core.domain.games.common.ObserveUseCaseParams
import ca.hojat.gamehub.core.domain.games.repository.GamesLocalDataSource
import ca.hojat.gamehub.core.domain.games.usecases.ObserveMostAnticipatedGamesUseCaseImpl
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAMES
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ObserveMostAnticipatedGamesUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var gamesLocalDataSource: GamesLocalDataSource

    private lateinit var sut: ObserveMostAnticipatedGamesUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = ObserveMostAnticipatedGamesUseCaseImpl(
            gamesLocalDataSource = gamesLocalDataSource,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits games successfully`() {
        runTest {
            every { gamesLocalDataSource.observeMostAnticipatedGames(any()) } returns flowOf(
                DOMAIN_GAMES
            )

            sut.execute(ObserveUseCaseParams()).test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }
}
