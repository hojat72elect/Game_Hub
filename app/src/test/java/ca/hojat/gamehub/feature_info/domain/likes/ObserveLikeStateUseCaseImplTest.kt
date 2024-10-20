package ca.hojat.gamehub.feature_info.domain.likes

import app.cash.turbine.test
import ca.hojat.gamehub.feature_info.OBSERVE_GAME_LIKE_STATE_USE_CASE_PARAMS
import ca.hojat.gamehub.core.domain.games.repository.LikedGamesLocalDataSource
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_info.domain.usecases.likes.ObserveLikeStateUseCaseImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ObserveLikeStateUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var likedGamesLocalDataSource: LikedGamesLocalDataSource

    private lateinit var sut: ObserveLikeStateUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        sut = ObserveLikeStateUseCaseImpl(
            likedGamesLocalDataSource = likedGamesLocalDataSource,
            dispatcherProvider = mainCoroutineRule.dispatcherProvider,
        )
    }

    @Test
    fun `Emits game like state successfully`() {
        runTest {
            every { likedGamesLocalDataSource.observeGameLikeState(any()) } returns flowOf(true)
            sut.execute(OBSERVE_GAME_LIKE_STATE_USE_CASE_PARAMS).test {
                assertThat(awaitItem()).isTrue()
                awaitComplete()
            }

            every { likedGamesLocalDataSource.observeGameLikeState(any()) } returns flowOf(false)
            sut.execute(OBSERVE_GAME_LIKE_STATE_USE_CASE_PARAMS).test {
                assertThat(awaitItem()).isFalse()
                awaitComplete()
            }
        }
    }
}
