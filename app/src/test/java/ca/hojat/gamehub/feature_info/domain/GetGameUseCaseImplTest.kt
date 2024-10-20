package ca.hojat.gamehub.feature_info.domain

import app.cash.turbine.test
import ca.hojat.gamehub.feature_info.GET_GAME_USE_CASE_PARAMS
import ca.hojat.gamehub.core.domain.games.repository.GamesLocalDataSource
import ca.hojat.gamehub.core.domain.entities.Error
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAME
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_info.domain.usecases.game.GetGameUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetGameUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var gamesLocalDataSource: GamesLocalDataSource

    private lateinit var sut: GetGameUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = GetGameUseCaseImpl(
            gamesLocalDataSource = gamesLocalDataSource,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits game successfully`() {
        runTest {
            coEvery { gamesLocalDataSource.getGame(any()) } returns DOMAIN_GAME

            sut.execute(GET_GAME_USE_CASE_PARAMS).test {
                assertThat(awaitItem().get()).isEqualTo(DOMAIN_GAME)
                awaitComplete()
            }
        }
    }

    @Test
    fun `Emits not found error if game ID does not reference existing game`() {
        runTest {
            coEvery { gamesLocalDataSource.getGame(any()) } returns null

            sut.execute(GET_GAME_USE_CASE_PARAMS).test {
                assertThat(awaitItem().getError() is Error.NotFound).isTrue()
                awaitComplete()
            }
        }
    }
}
