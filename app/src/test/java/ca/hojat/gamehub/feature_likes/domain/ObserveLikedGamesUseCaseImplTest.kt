package ca.hojat.gamehub.feature_likes.domain

import app.cash.turbine.test
import ca.hojat.gamehub.core.domain.games.common.ObserveUseCaseParams
import ca.hojat.gamehub.core.domain.games.repository.LikedGamesLocalDataSource
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

private val OBSERVE_GAMES_USE_CASE_PARAMS = ObserveUseCaseParams()

class ObserveLikedGamesUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var likedGamesLocalDataSource: LikedGamesLocalDataSource

    private lateinit var sut: ObserveLikedGamesUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = ObserveLikedGamesUseCaseImpl(
            likedGamesLocalDataSource = likedGamesLocalDataSource,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits liked games successfully`() {
        runTest {
            every { likedGamesLocalDataSource.observeLikedGames(any()) } returns flowOf(DOMAIN_GAMES)

            sut.execute(OBSERVE_GAMES_USE_CASE_PARAMS).test {
                assertThat(awaitItem()).isEqualTo(DOMAIN_GAMES)
                awaitComplete()
            }
        }
    }
}
